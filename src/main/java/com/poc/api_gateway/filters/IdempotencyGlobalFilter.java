package com.poc.api_gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class IdempotencyGlobalFilter implements GlobalFilter {
  final Logger logger = LoggerFactory.getLogger(IdempotencyGlobalFilter.class);

  @Value("${api-gateway.region}")
  private String region;

  private RedisTemplate<String, IdempotencyKV> redisTemplate;
  private MessageDigest digest;
  private ObjectMapper objectMapper;

  @Autowired
  public IdempotencyGlobalFilter(RedisTemplate redisTemplate) throws NoSuchAlgorithmException {
    digest = MessageDigest.getInstance("SHA-256");
    objectMapper = new ObjectMapper();
    this.redisTemplate = redisTemplate;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    logger.info("IdempotencyGlobalFilter");

    HttpMethod method = exchange.getRequest().getMethod();
    logger.info("Method: {}", method);
    String idempotencyKey = exchange.getRequest().getHeaders().getFirst("X-Idempotency-Key");
    logger.info("Idempotency Key: {}", idempotencyKey);
    String requestPath = exchange.getRequest().getPath().toString();
    logger.info("Request Path: {}", requestPath);
    // java code to hash the payload
    String requestPayloadHash =
        Hex.encodeHexString(
            digest.digest(
                exchange.getRequest().getBody().toString().getBytes(StandardCharsets.UTF_8)));

    // store result only for POST request & idempotency key is present
    if (isTargetMethod(method) && idempotencyKey != null) {
      logger.info("caching flow");
      // if idempotency key is present then check if it is already executed
      IdempotencyKV cachedValue = redisTemplate.opsForValue().get(idempotencyKey);
      logger.info("Cached Value: {}", cachedValue);
      if (cachedValue != null) {
        return Mono.defer(
            () -> {
              ServerHttpResponse response = exchange.getResponse();
              response.setStatusCode(cachedValue.status());
              return response.writeWith(
                  Mono.just(
                      response
                          .bufferFactory()
                          .wrap(cachedValue.toString().getBytes(StandardCharsets.UTF_8))));
            });
      } else {
        return chain
            .filter(exchange)
            .then(
                Mono.fromRunnable(
                    () -> {
                      ServerHttpResponse response = exchange.getResponse();
                      logger.info("Response: {}", response);
                      IdempotencyKV value =
                          new IdempotencyKV(
                              HttpStatus.valueOf(response.getStatusCode().value()),
                              requestPayloadHash,
                              region,
                              ZonedDateTime.now().toString());
                      try {
                        cacheHttpResponse(idempotencyKey, value);
                      } catch (JsonProcessingException e) {
                        logger.error(e.getMessage());
                      }
                    }));
      }
    } else {
      return chain.filter(exchange);
    }
  }

  private boolean isTargetMethod(HttpMethod method) {
    List<HttpMethod> methods = List.of(HttpMethod.POST, HttpMethod.GET, HttpMethod.PUT);
    return methods.contains(method);
  }

  private void cacheHttpResponse(String key, IdempotencyKV value) throws JsonProcessingException {
    logger.info("cacheHttpResponse {} {}", key, value);
    ValueOperations<String, IdempotencyKV> ops = redisTemplate.opsForValue();
    String strValue = objectMapper.writeValueAsString(value);
    ops.set(key.trim(), value, 1, TimeUnit.HOURS);
  }
}

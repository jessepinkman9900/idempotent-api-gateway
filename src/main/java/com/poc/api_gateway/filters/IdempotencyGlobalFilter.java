package com.poc.api_gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class IdempotencyGlobalFilter implements GlobalFilter {
    final Logger logger = LoggerFactory.getLogger(IdempotencyGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("IdempotencyGlobalFilter");
        logger.info("X-Idempotency-Key: {}", exchange.getRequest().getHeaders().get("X-Idempotency-Key").stream().findFirst().orElse("lol"));
        return chain.filter(exchange);
    }
}

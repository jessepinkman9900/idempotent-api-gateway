package com.poc.api_gateway.configs;

import com.poc.api_gateway.filters.IdempotencyKV;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class Redis {

  @Bean
  public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<?, ?> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);

    // for key - use redis string serializer
    template.setKeySerializer(new StringRedisSerializer());

    // for hash key - use redis string serializer
    template.setHashKeySerializer(new StringRedisSerializer());

    // for value - use jackson serializer
    Jackson2JsonRedisSerializer<IdempotencyKV> jackson2JsonRedisSerializer =
        new Jackson2JsonRedisSerializer<>(IdempotencyKV.class);
    template.setValueSerializer(jackson2JsonRedisSerializer);
    template.setHashValueSerializer(jackson2JsonRedisSerializer);

    return template;
  }
}

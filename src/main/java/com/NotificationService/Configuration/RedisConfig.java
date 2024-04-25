package com.NotificationService.Configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@EnableRedisRepositories
@Configuration
public class RedisConfig {
  @Bean
  RedisConnectionFactory ConnectionFactory() {
    return new LettuceConnectionFactory("redis",6379);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisConfigTemplate = new RedisTemplate<>();
    redisConfigTemplate.setEnableTransactionSupport(true);
    redisConfigTemplate.setConnectionFactory(ConnectionFactory());
    redisConfigTemplate.setKeySerializer(new StringRedisSerializer());
    redisConfigTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisConfigTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
    redisConfigTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
    redisConfigTemplate.afterPropertiesSet();
    return redisConfigTemplate;
  }
}

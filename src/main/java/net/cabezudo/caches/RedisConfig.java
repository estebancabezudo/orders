package net.cabezudo.caches;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class RedisConfig {
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig();
    return RedisCacheManager.builder(connectionFactory).cacheDefaults(cacheConfig).build();
  }
}

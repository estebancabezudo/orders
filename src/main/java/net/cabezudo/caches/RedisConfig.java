package net.cabezudo.caches;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class RedisConfig {

  @Bean
  public CacheManager cacheManager(@Value("${app.config.cache.enabled}") String isCacheEnabled, RedisConnectionFactory connectionFactory) {
    if (isCacheEnabled.equalsIgnoreCase("false")) {
      return new NoOpCacheManager();
    }
    RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig();
    return RedisCacheManager.builder(connectionFactory).cacheDefaults(cacheConfig).build();
  }
}

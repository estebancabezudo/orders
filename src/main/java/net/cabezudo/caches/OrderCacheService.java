package net.cabezudo.caches;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderCacheService {

  private StringRedisTemplate redisTemplate;

  @Autowired
  private void set(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void saveOrderToCache(String orderId, String orderData) {
    redisTemplate.opsForValue().set(orderId, orderData);
  }

  public String getOrderFromCache(String orderId) {
    return redisTemplate.opsForValue().get(orderId);
  }

  public void removeOrderFromCache(String orderId) {
    redisTemplate.delete(orderId);
  }
}

package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.port.CacheStorePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
@Service
@RequiredArgsConstructor
public class RedisCacheStoreAdapter<V> implements CacheStorePort<V> {
    private final ApplicationContext applicationContext;

    private RedisTemplate<String, V> getRedisTemplate(Class<V> type) {
        String beanName = Character.toLowerCase(type.getSimpleName().charAt(0)) + type.getSimpleName().substring(1) + "RedisTemplate";
        return (RedisTemplate<String, V>) applicationContext.getBean(beanName);
    }

    @Override
    public void put(String key, V value) {
        RedisTemplate<String, V> redisTemplate = getRedisTemplate((Class<V>) value.getClass());
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void put(String key, V value, long timeout, TimeUnit unit) {
        RedisTemplate<String, V> redisTemplate = getRedisTemplate((Class<V>) value.getClass());
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public V get(String key, Class<V> type) {
        RedisTemplate<String, V> redisTemplate = getRedisTemplate(type);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean containsKey(String key, Class<V> type) {
        RedisTemplate<String, V> redisTemplate = getRedisTemplate(type);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void deleteKey(String key, Class<V> type) {
        RedisTemplate<String, V> redisTemplate = getRedisTemplate(type);
        redisTemplate.delete(key);
    }
}

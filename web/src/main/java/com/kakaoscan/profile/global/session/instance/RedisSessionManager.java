package com.kakaoscan.profile.global.session.instance;

import com.kakaoscan.profile.domain.dto.UserDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Profile("prod")
public class RedisSessionManager implements SessionManager {
    private static final String HASH_KEY = "user:instance";

    private final RedisTemplate<String, UserDTO> redisTemplate;

    public RedisSessionManager(RedisTemplate<String, UserDTO> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setValue(String key, Object value) {
        if (value instanceof UserDTO) {
            redisTemplate.opsForHash().put(key, HASH_KEY, value);
            redisTemplate.expire(key, Duration.ofMinutes(60));
        }else {
            throw new IllegalArgumentException("check type: " + value.getClass().getName());
        }
    }

    @Override
    public Object getValue(String key) {
        return redisTemplate.opsForHash().get(key, HASH_KEY);
    }

    @Override
    public void deleteValue(String key) {
        if (redisTemplate.opsForHash().hasKey(key, HASH_KEY)) {
            redisTemplate.opsForHash().delete(key, HASH_KEY);
        }
    }
}
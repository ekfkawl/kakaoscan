package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.port.PhoneNumberCachePort;
import com.kakaoscan.server.domain.search.model.InvalidPhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisPhoneNumberCacheAdapter implements PhoneNumberCachePort {
    private final RedisTemplate<String, InvalidPhoneNumber> redisTemplate;

    private static final String INVALID_PHONE_NUMBER_KEY_PREFIX = "invalidPhoneNumber:";

    @Override
    public void cacheInvalidPhoneNumber(String phoneNumber, InvalidPhoneNumber invalidPhoneNumber) {
        ValueOperations<String, InvalidPhoneNumber> ops = redisTemplate.opsForValue();

        ops.set(INVALID_PHONE_NUMBER_KEY_PREFIX + phoneNumber, invalidPhoneNumber, 12, TimeUnit.HOURS);
    }

    @Override
    public boolean isInvalidPhoneNumberCached(String phoneNumber) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(INVALID_PHONE_NUMBER_KEY_PREFIX + phoneNumber));
    }
}

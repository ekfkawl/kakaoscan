package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.port.PointPort;
import com.kakaoscan.server.domain.point.model.Point;
import com.kakaoscan.server.domain.user.model.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PointAdapter implements PointPort {
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Integer> redisTemplate;

    private static final String LOCK_KEY_PREFIX = "userPointsLock:";
    private static final String POINT_CACHE_KEY_PREFIX = "pointCache:";
    private static final int LOCK_WAIT_TIME = 10;
    private static final int LOCK_LEASE_TIME = 30;

    @Override
    @Transactional(readOnly = true)
    public void cachePoints(String userId) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        ValueOperations<String, Integer> ops = redisTemplate.opsForValue();
        ops.set(POINT_CACHE_KEY_PREFIX + userId, user.getPoint().getBalance(), 1, TimeUnit.DAYS);
    }

    @Override
    public int getPointsFromCache(String userId) {
        ValueOperations<String, Integer> ops = redisTemplate.opsForValue();

        Integer points = ops.get(POINT_CACHE_KEY_PREFIX + userId);
        if (points == null) {
            throw new NullPointerException("points not found from cache");
        }

        return points;
    }

    @Override
    @Transactional
    public boolean deductPoints(String userId, int value) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + userId);

        try {
            if (!lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                return false;
            }

            User user = userRepository.findByEmail(userId)
                    .orElseThrow(() -> new IllegalArgumentException("user not found"));

            Point point = user.getPoint();
            if (point.getBalance() < value) {
                throw new IllegalStateException("not enough points");
            }

            point.deductBalance(value);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    lock.unlock();
                }
            });

            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("lock acquisition interrupted");
        }
    }
}

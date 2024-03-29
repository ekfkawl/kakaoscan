package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.port.CacheStorePort;
import com.kakaoscan.server.domain.point.entity.Point;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final CacheStorePort<Integer> cacheStorePort;

    private static final String LOCK_KEY_PREFIX = "userPointsLock:";
    private static final String POINT_CACHE_KEY_PREFIX = "pointCache:";
    private static final int LOCK_WAIT_TIME = 10;
    private static final int LOCK_LEASE_TIME = 30;

    @Transactional(readOnly = true)
    public void cachePoints(String userId, int value) {
        cacheStorePort.put(POINT_CACHE_KEY_PREFIX + userId, value, 1, TimeUnit.DAYS);
    }

    public int getPointsFromCache(String userId) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + userId);
        if (lock.isLocked()) {
            throw new ConcurrentModificationException("points data is currently being modified");
        }

        Integer points = cacheStorePort.get(POINT_CACHE_KEY_PREFIX + userId, Integer.class);
        if (points == null) {
            throw new NullPointerException("points not found from cache");
        }

        return points;
    }

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

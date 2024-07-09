package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.port.CacheStorePort;
import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.search.repository.SearchHistoryRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.cache.CacheUpdateObserver;
import com.kakaoscan.server.infrastructure.redis.utils.RedisCacheUtil;
import com.kakaoscan.server.infrastructure.redis.utils.RedissonLockUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.kakaoscan.server.infrastructure.constants.RedisKeyPrefixes.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointService {
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final CacheStorePort<Integer> integerCacheStorePort;
    private final CacheStorePort<SearchCost> costCacheStorePort;
    private final SearchHistoryRepository searchHistoryRepository;
    private final CacheUpdateObserver cacheUpdateObserver;

    @Transactional(readOnly = true)
    public int getPoints(String userId) {
        RLock lock = redissonClient.getLock(LOCK_USER_POINTS_KEY_PREFIX + userId);
        if (lock.isLocked()) {
            throw new ConcurrentModificationException("points data is currently being modified");
        }

        final String key = POINT_CACHE_KEY_PREFIX + userId;
        Supplier<Integer> supplier = () -> {
            User user = userRepository.findByEmailOrThrow(userId);
            return user.getPointWallet().getBalance();
        };
        return RedisCacheUtil.getFromCacheOrSupplier(integerCacheStorePort, key, Integer.class, supplier, 5, TimeUnit.MINUTES);
    }

    @Transactional
    public boolean deductPoints(String userId, int value) {
        RLock lock = redissonClient.getLock(LOCK_USER_POINTS_KEY_PREFIX + userId);

        return RedissonLockUtil.withLock(lock, () -> {
            User user = userRepository.findByEmailOrThrow(userId);

            PointWallet pointWallet = user.getPointWallet();
            if (pointWallet.getBalance() < value) {
                throw new IllegalStateException("not enough points");
            }

            pointWallet.deductBalance(value);
            cacheUpdateObserver.update(userId, pointWallet.getBalance());
        });
    }

    public void cacheTargetSearchCost(String userId, String targetPhoneNumber, SearchCost searchCost) {
        final String key = TARGET_SEARCH_COST_KEY_PREFIX + userId + targetPhoneNumber;
        costCacheStorePort.put(key, searchCost, 1, TimeUnit.MINUTES);
    }

    @Transactional(readOnly = true)
    public SearchCost getTargetSearchCost(String userId, String targetPhoneNumber) {
        final String key = TARGET_SEARCH_COST_KEY_PREFIX + userId + targetPhoneNumber;
        Supplier<SearchCost> supplier = () -> {
            User user = userRepository.findByEmailOrThrow(userId);
            return searchHistoryRepository.getTargetSearchCost(user, targetPhoneNumber);
        };
        return RedisCacheUtil.getFromCacheOrSupplier(costCacheStorePort, key, SearchCost.class, supplier, 1, TimeUnit.MINUTES);
    }
}

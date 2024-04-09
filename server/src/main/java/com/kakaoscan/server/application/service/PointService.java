package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.request.PointPaymentRequest;
import com.kakaoscan.server.application.exception.PendingTransactionExistsException;
import com.kakaoscan.server.application.port.CacheStorePort;
import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.point.repository.PointWalletRepository;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.product.repository.ProductTransactionRepository;
import com.kakaoscan.server.domain.search.repository.SearchHistoryRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.config.WordProperties;
import com.kakaoscan.server.infrastructure.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointService {
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final CacheStorePort<Integer> integerCacheStorePort;
    private final CacheStorePort<SearchCost> costCacheStorePort;
    private final SearchHistoryRepository searchHistoryRepository;
    private final PointWalletRepository pointWalletRepository;
    private final ProductTransactionRepository productTransactionRepository;
    private final WordProperties wordProperties;

    private static final String LOCK_KEY_PREFIX = "userPointsLock:";
    private static final String POINT_CACHE_KEY_PREFIX = "pointCache:";
    private static final String TARGET_SEARCH_COST_KEY_PREFIX = "targetSearchCost:";

    private static final int LOCK_WAIT_TIME = 10;
    private static final int LOCK_LEASE_TIME = 30;

    public void cachePoints(String userId, int value) {
        integerCacheStorePort.put(POINT_CACHE_KEY_PREFIX + userId, value, 5, TimeUnit.MINUTES);
    }

    @Transactional(readOnly = true)
    public int getAndCachePoints(String userId) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + userId);
        if (lock.isLocked()) {
            throw new ConcurrentModificationException("points data is currently being modified");
        }

        Integer points = integerCacheStorePort.get(POINT_CACHE_KEY_PREFIX + userId, Integer.class);
        if (points != null) {
            return points;
        }

        User user = userRepository.findByEmailOrThrow(userId);

        cachePoints(userId, user.getPointWallet().getBalance());

        return user.getPointWallet().getBalance();
    }

    @Transactional
    public boolean deductPoints(String userId, int value) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + userId);

        try {
            if (!lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                return false;
            }

            User user = userRepository.findByEmailOrThrow(userId);

            PointWallet pointWallet = user.getPointWallet();
            if (pointWallet.getBalance() < value) {
                throw new IllegalStateException("not enough points");
            }

            pointWallet.deductBalance(value);

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

    public void cacheTargetSearchCost(String userId, String targetPhoneNumber, SearchCost searchCost) {
        final String key = TARGET_SEARCH_COST_KEY_PREFIX + userId + targetPhoneNumber;

        costCacheStorePort.put(key, searchCost, 1, TimeUnit.MINUTES);
    }

    @Transactional(readOnly = true)
    public SearchCost getAndCacheTargetSearchCost(String userId, String targetPhoneNumber) {
        final String key = TARGET_SEARCH_COST_KEY_PREFIX + userId + targetPhoneNumber;

        SearchCost searchCost = costCacheStorePort.get(key, SearchCost.class);
        if (searchCost != null) {
            return searchCost;
        }

        User user = userRepository.findByEmailOrThrow(userId);

        searchCost = searchHistoryRepository.getTargetSearchCost(user, targetPhoneNumber);
        cacheTargetSearchCost(userId, targetPhoneNumber, searchCost);

        return searchCost;
    }

    @Transactional
    public void pendPointPayment(String userId, PointPaymentRequest paymentRequest) {
        User user = userRepository.findByEmailOrThrow(userId);

        if (productTransactionRepository.existsPendingTransaction(user.getPointWallet())) {
            throw new PendingTransactionExistsException("이미 결제 신청 내역이 존재합니다.");
        }

        List<ProductTransaction> pendingTransactions = productTransactionRepository.findByTransactionStatus(ProductTransactionStatus.PENDING);
        Set<String> depositorSet = pendingTransactions.stream()
                .map(ProductTransaction::getDepositor)
                .collect(Collectors.toSet());

        String combination;
        int attempts = 0;
        final int maxAttempts = 255;
        do {
            combination = wordProperties.combination();
            if (++attempts > maxAttempts) {
                log.error("depositor generate error");
                throw new IllegalStateException("현재 결제 신청이 불가합니다.");
            }
        } while (depositorSet.contains(combination));

        user.getPointWallet().addTransaction(paymentRequest, combination);
    }

    @Transactional
    public void cancelPointPayment(String userId, long transactionId) {
        User user = userRepository.findByEmailOrThrow(userId);

        Optional<ProductTransaction> optionalProductTransaction = productTransactionRepository.findByIdAndWallet(transactionId, user.getPointWallet());
        if (optionalProductTransaction.isEmpty()) {
            throw new DataNotFoundException("해당 내역은 존재하지 않습니다.");
        }

        switch (optionalProductTransaction.get().getTransactionStatus()) {
            case PENDING -> optionalProductTransaction.get().cancelTransaction();
            case EARNED -> throw new IllegalStateException("이미 결제가 완료된 내역입니다. 결제 취소가 불가능합니다.");
        }
    }
}

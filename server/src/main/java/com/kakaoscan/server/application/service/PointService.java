package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.request.PointPaymentRequest;
import com.kakaoscan.server.application.dto.request.WebhookProductOrderRequest;
import com.kakaoscan.server.application.exception.PendingTransactionExistsException;
import com.kakaoscan.server.application.port.CacheStorePort;
import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.product.model.ProductOrderClient;
import com.kakaoscan.server.domain.product.repository.ProductTransactionRepository;
import com.kakaoscan.server.domain.search.repository.SearchHistoryRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.config.WordProperties;
import com.kakaoscan.server.infrastructure.exception.DataNotFoundException;
import com.kakaoscan.server.infrastructure.redis.utils.RedisCacheUtil;
import com.kakaoscan.server.infrastructure.redis.utils.RedissonLockUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
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
    private final ProductTransactionRepository productTransactionRepository;
    private final ProductOrderClient productOrderClient;
    private final WordProperties wordProperties;

    private static final String LOCK_USER_POINTS_KEY_PREFIX = "userPointsLock:";
    private static final String LOCK_PEND_POINTS_PAYMENT_KEY_PREFIX = "pendPointsPaymentLock:";
    private static final String POINT_CACHE_KEY_PREFIX = "pointCache:";
    private static final String TARGET_SEARCH_COST_KEY_PREFIX = "targetSearchCost:";


    public void cachePoints(String userId, int value) {
        integerCacheStorePort.put(POINT_CACHE_KEY_PREFIX + userId, value, 5, TimeUnit.MINUTES);
    }

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

    @Transactional
    public boolean pendPointPayment(String userId, PointPaymentRequest paymentRequest) {
        RLock lock = redissonClient.getLock(LOCK_PEND_POINTS_PAYMENT_KEY_PREFIX + userId);

        return RedissonLockUtil.withLock(lock, () -> {
            User user = userRepository.findByEmailOrThrow(userId);

            if (productTransactionRepository.existsPendingTransaction(user.getPointWallet())) {
                throw new PendingTransactionExistsException("이미 결제 신청 내역이 존재합니다.");
            }

            List<ProductTransaction> pendingTransactions = productTransactionRepository.findByTransactionStatus(ProductTransactionStatus.PENDING);
            Set<String> depositorSet = pendingTransactions.stream()
                    .map(ProductTransaction::getDepositor)
                    .collect(Collectors.toSet());

            String uniqueDepositor = wordProperties.generateUniqueDepositor(depositorSet);

            ProductTransaction productTransaction = user.getPointWallet().addTransaction(paymentRequest, uniqueDepositor);
            productTransaction = productTransactionRepository.save(productTransaction);

            productOrderClient.createProductOrder(WebhookProductOrderRequest.builder()
                    .orderNumber(productTransaction.getId().toString())
                    .orderAmount(paymentRequest.getAmount())
                    .ordererName(uniqueDepositor)
                    .billingName(uniqueDepositor)
                    .build());
        });
    }

    @Transactional
    public void cancelPointPayment(String userId, long transactionId) {
        User user = userRepository.findByEmailOrThrow(userId);

        Optional<ProductTransaction> optionalProductTransaction = productTransactionRepository.findByIdAndWallet(transactionId, user.getPointWallet());
        if (optionalProductTransaction.isEmpty()) {
            throw new DataNotFoundException("해당 내역은 존재하지 않습니다.");
        }

        switch (optionalProductTransaction.get().getTransactionStatus()) {
            case PENDING -> {
                optionalProductTransaction.get().cancel();
                productOrderClient.excludeProductOrder(new WebhookProductOrderRequest(optionalProductTransaction.get().getId().toString()));
            }
            case EARNED -> throw new IllegalStateException("이미 결제가 완료된 내역입니다. 결제 취소가 불가능합니다.");
        }
    }
}

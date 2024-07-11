package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.domain.item.entity.UserItem;
import com.kakaoscan.server.domain.item.repository.UserItemRepository;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.kakaoscan.server.infrastructure.constants.RedisKeyPrefixes.LOCK_SNAPSHOT_PRESERVATION_PAYMENT_KEY_PREFIX;

@Log4j2
@Service
@RequiredArgsConstructor
public class SnapshotItemTransactionProcessor extends ProductTransactionProcessor<ProductTransaction> {
    private final UserRepository userRepository;
    private final UserItemRepository userItemRepository;
    private final RedissonClient redissonClient;

    @Override
    public ProductType getProductType() {
        return ProductType.SNAPSHOT_PRESERVATION;
    }

    @Override
    public String getLockPrefix() {
        return LOCK_SNAPSHOT_PRESERVATION_PAYMENT_KEY_PREFIX;
    }

    @Override
    public void approve(ProductTransaction transaction) {
        transaction.getUser().addUserItem(UserItem.builder()
                .productType(ProductType.SNAPSHOT_PRESERVATION)
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build());
    }

    @Override
    public void cancelApproval(ProductTransaction transaction) {

    }
}

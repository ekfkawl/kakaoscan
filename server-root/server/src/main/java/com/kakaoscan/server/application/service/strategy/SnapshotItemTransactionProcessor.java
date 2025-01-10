package com.kakaoscan.server.application.service.strategy;

import com.kakaoscan.server.domain.item.entity.UserItem;
import com.kakaoscan.server.domain.item.repository.UserItemRepository;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kakaoscan.server.infrastructure.constants.RedisKeyPrefixes.LOCK_SNAPSHOT_PRESERVATION_PAYMENT_KEY_PREFIX;

@Log4j2
@Service
@RequiredArgsConstructor
public class SnapshotItemTransactionProcessor extends ProductTransactionProcessor<ProductTransaction> {
    private final UserItemRepository userItemRepository;

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
        Optional<UserItem> optionalUserItem = userItemRepository.findByUserAndProductType(transaction.getUser(), this.getProductType());
        if (optionalUserItem.isEmpty()) {
            transaction.getUser().addUserItem(UserItem.builder()
                    .productType(this.getProductType())
                    .expiredAt(LocalDateTime.now().plusDays(30))
                    .build());
        }else {
            optionalUserItem.get().renew();
        }
    }

    @Override
    public void cancelApproval(ProductTransaction transaction) {
        userItemRepository.findByUserAndProductType(transaction.getUser(), this.getProductType())
                .ifPresent(userItem -> {
                    userItem.getUser().removeUserItem(userItem);
                    userItemRepository.delete(userItem);
                });
    }
}

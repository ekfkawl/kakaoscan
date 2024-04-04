package com.kakaoscan.server.domain.point.repository;

import com.kakaoscan.server.domain.point.entity.PointTransaction;
import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.point.entity.QPointTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomPointWalletRepositoryImpl implements CustomPointWalletRepository {
    private final JPAQueryFactory factory;

    @Override
    public boolean existsPendingTransaction(PointWallet pointWallet) {
        QPointTransaction pointTransaction = QPointTransaction.pointTransaction;

        return factory.selectOne()
                .from(pointTransaction)
                .where(pointTransaction.wallet.eq(pointWallet)
                        .and(pointTransaction.transactionStatus.eq(ProductTransactionStatus.PENDING)))
                .fetchFirst() != null;
    }

    @Override
    public List<PointTransaction> findTransactionsByDateRange(PointWallet pointWallet, LocalDateTime startDate, LocalDateTime endDate) {
        QPointTransaction pointTransaction = QPointTransaction.pointTransaction;

        return factory.selectFrom(pointTransaction)
                .where(pointTransaction.wallet.eq(pointWallet)
                        .and(pointTransaction.createdAt.goe(startDate))
                        .and(pointTransaction.createdAt.loe(endDate)))
                .orderBy(pointTransaction.createdAt.desc())
                .fetch();
    }
}

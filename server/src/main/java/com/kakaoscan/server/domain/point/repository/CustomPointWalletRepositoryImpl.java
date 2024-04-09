package com.kakaoscan.server.domain.point.repository;

import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.entity.QProductTransaction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomPointWalletRepositoryImpl implements CustomPointWalletRepository {
    private final JPAQueryFactory factory;

    @Override
    public List<ProductTransaction> findProductTransactionsByPointWallet(PointWallet pointWallet, LocalDateTime startDate, LocalDateTime endDate) {
        QProductTransaction productTransaction = QProductTransaction.productTransaction;

        return factory.selectFrom(productTransaction)
                .where(productTransaction.wallet.eq(pointWallet)
                        .and(productTransaction.createdAt.goe(startDate))
                        .and(productTransaction.createdAt.loe(endDate)))
                .orderBy(productTransaction.createdAt.desc())
                .fetch();
    }
}

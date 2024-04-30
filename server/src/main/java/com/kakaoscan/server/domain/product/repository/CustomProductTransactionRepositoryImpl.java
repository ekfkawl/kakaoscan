package com.kakaoscan.server.domain.product.repository;

import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.point.entity.QPointWallet;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.entity.QProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.user.entity.QUser;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomProductTransactionRepositoryImpl implements CustomProductTransactionRepository {
    private final JPAQueryFactory factory;

    @Override
    public boolean existsPendingTransaction(PointWallet pointWallet) {
        QProductTransaction productTransaction = QProductTransaction.productTransaction;

        return factory.selectOne()
                .from(productTransaction)
                .where(productTransaction.wallet.eq(pointWallet)
                        .and(productTransaction.transactionStatus.eq(ProductTransactionStatus.PENDING)))
                .fetchFirst() != null;
    }

    @Override
    public QueryResults<ProductTransaction> findAndFilterTransactions(LocalDateTime startDate, LocalDateTime endDate, ProductTransactionStatus status, String keyword, int page, int pageSize) {
        QProductTransaction productTransaction = QProductTransaction.productTransaction;
        QPointWallet pointWallet = QPointWallet.pointWallet;
        QUser user = QUser.user;

        BooleanExpression whereCondition = productTransaction.createdAt.goe(startDate)
                .and(productTransaction.createdAt.loe(endDate))
                .and(status != null ? productTransaction.transactionStatus.eq(status) : null);

        if (keyword != null && !keyword.trim().isEmpty()) {
            BooleanExpression keywordCondition = productTransaction.depositor.likeIgnoreCase(keyword + '%')
                    .or(productTransaction.wallet.user.email.likeIgnoreCase(keyword + '%'));

            whereCondition = whereCondition.and(keywordCondition);
        }

        return factory.selectFrom(productTransaction)
                .leftJoin(productTransaction.wallet, pointWallet)
                .leftJoin(pointWallet.user, user)
                .where(whereCondition)
                .orderBy(productTransaction.createdAt.desc())
                .offset((long) (page - 1) * pageSize)
                .limit(pageSize)
                .fetchResults();
    }

    @Override
    public List<ProductTransaction> findOldPendingTransactions() {
        QProductTransaction productTransaction = QProductTransaction.productTransaction;

        return factory
                .selectFrom(productTransaction)
                .where(productTransaction.transactionStatus.eq(ProductTransactionStatus.PENDING)
                        .and(productTransaction.createdAt.before(LocalDateTime.now().minusHours(24))))
                .fetch();
    }
}

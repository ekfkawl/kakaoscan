package com.kakaoscan.server.domain.product.repository;

import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.querydsl.core.QueryResults;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomProductTransactionRepository {
    boolean existsPendingTransaction(PointWallet pointWallet);

    QueryResults<ProductTransaction> findAndFilterTransactions(LocalDateTime startDate, LocalDateTime endDate, ProductTransactionStatus status, String keyword, int page, int pageSize);

    List<ProductTransaction> findOldPendingTransactions();
}
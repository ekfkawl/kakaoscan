package com.kakaoscan.server.domain.point.repository;

import com.kakaoscan.server.domain.point.entity.PointTransaction;
import com.kakaoscan.server.domain.point.entity.PointWallet;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomPointWalletRepository {
    boolean existsPendingTransaction(PointWallet pointWallet);

    List<PointTransaction> findTransactionsByDateRange(PointWallet pointWallet, LocalDateTime startDate, LocalDateTime endDate);
}

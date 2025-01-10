package com.kakaoscan.server.domain.point.repository;

import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomPointWalletRepository {

    List<ProductTransaction> findProductTransactionsByPointWallet(PointWallet pointWallet, LocalDateTime startDate, LocalDateTime endDate);
}

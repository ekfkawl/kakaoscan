package com.kakaoscan.server.domain.point.repository;

import com.kakaoscan.server.domain.point.entity.PointTransaction;
import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    Optional<PointTransaction> findByIdAndWallet(Long id, PointWallet wallet);

    List<PointTransaction> findByTransactionStatus(ProductTransactionStatus transactionStatus);
}

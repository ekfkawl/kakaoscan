package com.kakaoscan.server.domain.product.repository;

import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long>, CustomProductTransactionRepository {
    Optional<ProductTransaction> findByIdAndWallet(Long id, PointWallet wallet);

    List<ProductTransaction> findByTransactionStatus(ProductTransactionStatus transactionStatus);

    @Modifying
    @Query("DELETE FROM ProductTransaction pt WHERE pt.wallet = :wallet")
    void deleteByWallet(PointWallet wallet);
}

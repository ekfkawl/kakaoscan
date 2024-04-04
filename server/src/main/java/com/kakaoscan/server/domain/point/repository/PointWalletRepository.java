package com.kakaoscan.server.domain.point.repository;

import com.kakaoscan.server.domain.point.entity.PointWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointWalletRepository extends JpaRepository<PointWallet, Long>, CustomPointWalletRepository {
}

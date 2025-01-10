package com.kakaoscan.server.infrastructure.persistence.repository;

import com.kakaoscan.server.infrastructure.persistence.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long>, CustomLogRepository {
}

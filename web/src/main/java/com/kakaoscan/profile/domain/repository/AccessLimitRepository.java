package com.kakaoscan.profile.domain.repository;

import com.kakaoscan.profile.domain.entity.AccessLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AccessLimitRepository extends JpaRepository<AccessLimit, LocalDate> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select a from tb_access_limit a where a.date = :localDate")
    Optional<AccessLimit> findLockById(LocalDate localDate);
}

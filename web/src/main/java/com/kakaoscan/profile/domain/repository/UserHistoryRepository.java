package com.kakaoscan.profile.domain.repository;

import com.kakaoscan.profile.domain.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    Optional<List<UserHistory>> findByEmail(String email);
    Optional<List<UserHistory>> findByEmailOrderByModifyDtDesc(String email);
    Optional<UserHistory> findByEmailAndPhoneNumber(String email, String phoneNumber);
    Optional<List<UserHistory>> findByModifyDtBefore(LocalDateTime createDt);
}


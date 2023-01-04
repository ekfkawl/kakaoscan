package com.kakaoscan.profile.domain.repository;

import com.kakaoscan.profile.domain.entity.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRequestRepository extends JpaRepository<UserRequest, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select u from tb_user_request u where u.remoteAddress = :remoteAddress")
    Optional<UserRequest> findLockById(String remoteAddress);

    Optional<UserRequest> findByRemoteAddressAndLastUseDt(String remoteAddress, LocalDate localDate);
}

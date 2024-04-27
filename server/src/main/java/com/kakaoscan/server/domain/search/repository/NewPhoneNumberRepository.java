package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.search.entity.NewPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewPhoneNumberRepository extends JpaRepository<NewPhoneNumber, Long>, CustomNewPhoneNumberRepository {
    Optional<NewPhoneNumber> findByTargetPhoneNumber(String targetPhoneNumber);
}

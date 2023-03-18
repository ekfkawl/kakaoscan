package com.kakaoscan.profile.domain.repository;

import com.kakaoscan.profile.domain.entity.AddedNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddedNumberRepository extends JpaRepository<AddedNumber, String> {
    boolean existsByPhoneNumberHash(String phoneNumber);
}

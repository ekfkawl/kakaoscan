package com.kakaoscan.profile.domain.repository;

import com.kakaoscan.profile.domain.entity.UserRequestUnlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestUnlockRepository extends JpaRepository<UserRequestUnlock, String> {
}

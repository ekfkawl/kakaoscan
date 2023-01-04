package com.kakaoscan.profile.domain.repository;

import com.kakaoscan.profile.domain.entity.Cache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheRepository extends JpaRepository<Cache, String> {
}

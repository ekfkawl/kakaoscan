package com.kakaoscan.profile.domain.repository;

import com.kakaoscan.profile.domain.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(attributePaths = "historyList")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"requestUnlock", "request", "historyList"})
    List<User> findAll(Sort sort);
}

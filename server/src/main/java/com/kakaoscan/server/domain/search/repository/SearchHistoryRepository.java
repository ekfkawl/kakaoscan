package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long>, CustomSearchHistoryRepository {
    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.user = :user")
    void deleteByUser(User user);
}

package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.search.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long>, CustomSearchHistoryRepository {
}

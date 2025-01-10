package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomSearchHistoryRepository {
    SearchCost getTargetSearchCost(User user, String targetPhoneNumber);

    List<SearchHistory> findRecentSearchHistories(User user, LocalDateTime threshold);
}

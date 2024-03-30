package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.user.entity.User;

public interface CustomSearchHistoryRepository {
    SearchCost getTargetSearchCost(User user, String targetPhoneNumber);
}

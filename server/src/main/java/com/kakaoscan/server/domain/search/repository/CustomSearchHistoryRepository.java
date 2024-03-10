package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.search.enums.CostType;
import com.kakaoscan.server.domain.user.entity.User;

public interface CustomSearchHistoryRepository {
    CostType getCurrentCostType(User user, String targetPhoneNumber);
}

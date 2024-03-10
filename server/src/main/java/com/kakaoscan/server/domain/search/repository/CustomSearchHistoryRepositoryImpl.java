package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.search.entity.QSearchHistory;
import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.search.enums.CostType;
import com.kakaoscan.server.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CustomSearchHistoryRepositoryImpl implements CustomSearchHistoryRepository {
    private final JPAQueryFactory factory;

    @Override
    public CostType getCurrentCostType(User user, String targetPhoneNumber) {
        QSearchHistory searchHistory = QSearchHistory.searchHistory;

        SearchHistory recentHistory = factory.selectFrom(searchHistory)
                .where(searchHistory.user.eq(user)
                        .and(searchHistory.targetPhoneNumber.eq(targetPhoneNumber))
                        .and(searchHistory.costType.eq(CostType.ORIGIN))
                        .and(searchHistory.createdAt.after(LocalDateTime.now().minusHours(24))))
                .orderBy(searchHistory.createdAt.desc())
                .fetchFirst();

        if (recentHistory != null) {
            return CostType.DISCOUNT;
        } else {
            return CostType.ORIGIN;
        }
    }
}

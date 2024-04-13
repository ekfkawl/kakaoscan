package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.search.entity.QSearchHistory;
import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.search.enums.CostType;
import com.kakaoscan.server.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CustomSearchHistoryRepositoryImpl implements CustomSearchHistoryRepository {
    private final JPAQueryFactory factory;

    @Value("${search.profile.cost.origin}")
    private int costOrigin;

    @Value("${search.profile.cost.discount}")
    private int costDiscount;

    private static final int costFree = 0;

    @Override
    public SearchCost getTargetSearchCost(User user, String targetPhoneNumber) {
        QSearchHistory searchHistory = QSearchHistory.searchHistory;

        SearchHistory lastOriginHistory = factory.selectFrom(searchHistory)
                .where(searchHistory.user.eq(user)
                        .and(searchHistory.targetPhoneNumber.eq(targetPhoneNumber))
                        .and(searchHistory.costType.eq(CostType.ORIGIN))
                        .and(searchHistory.createdAt.after(LocalDateTime.now().minusHours(24))))
                .orderBy(searchHistory.createdAt.desc())
                .fetchFirst();

        SearchHistory lastNonFreeHistory = factory.selectFrom(searchHistory)
                .where(searchHistory.user.eq(user)
                        .and(searchHistory.targetPhoneNumber.eq(targetPhoneNumber))
                        .and(searchHistory.costType.ne(CostType.FREE))
                        .and(searchHistory.createdAt.after(LocalDateTime.now().minusMinutes(10))))
                .orderBy(searchHistory.createdAt.desc())
                .fetchFirst();

        LocalDateTime expiredAtDiscount = null;
        CostType costType = CostType.ORIGIN;
        int cost = costOrigin;

        if (lastNonFreeHistory != null) {
            costType = CostType.FREE;
            cost = costFree;
            expiredAtDiscount = lastNonFreeHistory.getCreatedAt().plusMinutes(10);
        }else if (lastOriginHistory != null) {
            costType = CostType.DISCOUNT;
            cost = costDiscount;
            expiredAtDiscount = lastOriginHistory.getCreatedAt().plusHours(24);
        }

        return new SearchCost(costType, cost, expiredAtDiscount);
    }
}

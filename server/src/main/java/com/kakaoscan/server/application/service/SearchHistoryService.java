package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.response.SearchHistories;
import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.search.enums.CostType;
import com.kakaoscan.server.domain.search.model.SearchResult;
import com.kakaoscan.server.domain.search.repository.SearchHistoryRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.serialization.JsonDeserialize;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    @Transactional(readOnly = true)
    public SearchHistories findSearchHistories(String userId) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        SearchHistories result = new SearchHistories();

        List<SearchHistory> searchHistories = user.getSearchHistories();
        for (SearchHistory searchHistory : searchHistories) {
            if (LocalDateTime.now().isAfter(searchHistory.getCreatedAt().plusDays(7))) {
                continue;
            }

            SearchResult searchResult = JsonDeserialize.deserialize(searchHistory.getData(), SearchResult.class);
            result.addHistory(searchResult, searchHistory.getTargetPhoneNumber(), searchHistory.getCreatedAt());
        }

        return result;
    }

    @Transactional
    public void recordUserSearchHistory(String userId, String targetPhoneNumber, String data) {
        userRepository.findByEmail(userId).ifPresent(user -> {
            CostType costType = searchHistoryRepository.getCurrentCostType(user, targetPhoneNumber);

            SearchHistory searchHistory = SearchHistory.builder()
                    .targetPhoneNumber(targetPhoneNumber)
                    .data(data)
                    .costType(costType)
                    .createdAt(LocalDateTime.now())
                    .build();

            user.addSearchHistory(searchHistory);
        });
    }
}

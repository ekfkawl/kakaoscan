package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.response.SearchHistories;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.search.entity.NewPhoneNumber;
import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.search.enums.CostType;
import com.kakaoscan.server.domain.search.model.SearchResult;
import com.kakaoscan.server.domain.search.repository.SearchHistoryRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.serialization.JsonDeserialize;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    @Transactional(readOnly = true)
    public SearchHistories findUserSearchHistories(String userId) {
        User user = userRepository.findByEmailOrThrow(userId);

        SearchHistories result = new SearchHistories();

        LocalDateTime threshold = LocalDateTime.now().minusDays(2);
        List<SearchHistory> searchHistories = searchHistoryRepository.findRecentSearchHistories(user, threshold);

        for (SearchHistory searchHistory : searchHistories) {
            SearchResult searchResult = JsonDeserialize.deserialize(searchHistory.getData(), SearchResult.class);
            result.addHistory(searchResult, searchHistory.getTargetPhoneNumber(), searchHistory.getCostType().getCost(), searchHistory.getCreatedAt());
        }

        return result;
    }

    @Transactional
    public void recordUserSearchHistory(String userId, String targetPhoneNumber, String data, CostType costType) {
        User user = userRepository.findByEmailOrThrow(userId);

        SearchHistory searchHistory = SearchHistory.builder()
                .targetPhoneNumber(targetPhoneNumber)
                .data(data)
                .costType(costType)
                .createdAt(LocalDateTime.now())
                .build();

        user.addSearchHistory(searchHistory);
    }

    @Transactional(readOnly = true)
    public SearchCost getTargetSearchCost(String userId, String targetPhoneNumber) {
        User user = userRepository.findByEmailOrThrow(userId);

        return searchHistoryRepository.getTargetSearchCost(user, targetPhoneNumber);
    }

    @Transactional
    public void recordNewPhoneNumber(String userId, String targetPhoneNumber) {
        User user = userRepository.findByEmailOrThrow(userId);

        user.addNewPhoneNumbers(NewPhoneNumber.builder()
                .targetPhoneNumber(targetPhoneNumber)
                .createdAt(LocalDateTime.now())
                .build());
    }
}

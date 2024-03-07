package com.kakaoscan.server.application.service;

import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {
    private final UserRepository userRepository;

    @Transactional
    public void recordUserSearchHistory(String userId, String targetPhoneNumber, String data) {
        userRepository.findByEmail(userId).ifPresent(user -> {
            SearchHistory searchHistory = SearchHistory.builder()
                    .user(user)
                    .targetPhoneNumber(targetPhoneNumber)
                    .data(data)
                    .build();

            user.getSearchHistories().add(searchHistory);
        });
    }
}

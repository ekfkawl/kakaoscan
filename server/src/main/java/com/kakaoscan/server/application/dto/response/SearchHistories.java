package com.kakaoscan.server.application.dto.response;

import com.kakaoscan.server.domain.search.model.SearchResult;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SearchHistories {
    private final List<SearchResultResponse> histories;

    public SearchHistories() {
        this.histories = new ArrayList<>();
    }

    public void addHistory(SearchResult searchResult, String targetPhoneNumber, int cost, LocalDateTime createdAt) {
        this.histories.add(new SearchResultResponse(searchResult.getProfile(), searchResult.getStatus(), targetPhoneNumber, cost, createdAt));
    }

    @Getter
    private static class SearchResultResponse extends SearchResult {
        private final String targetPhoneNumber;
        private final int cost;
        private final LocalDateTime createdAt;

        public SearchResultResponse(Profile profile, int status, String targetPhoneNumber, int cost, LocalDateTime createdAt) {
            super(profile, status);
            this.targetPhoneNumber = targetPhoneNumber;
            this.cost = cost;
            this.createdAt = createdAt;
        }
    }
}

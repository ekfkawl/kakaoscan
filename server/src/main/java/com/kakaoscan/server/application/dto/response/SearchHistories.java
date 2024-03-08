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

    public void addHistory(SearchResult searchResult, String targetPhoneNumber, LocalDateTime createdAt) {
        this.histories.add(new SearchResultResponse(searchResult.getProfile(), searchResult.getStatus(), targetPhoneNumber, createdAt));
    }

    @Getter
    private static class SearchResultResponse extends SearchResult {
        private final String targetPhoneNumber;
        private final LocalDateTime createdAt;

        public SearchResultResponse(Profile profile, int status, String targetPhoneNumber, LocalDateTime createdAt) {
            super(profile, status);
            this.targetPhoneNumber = targetPhoneNumber;
            this.createdAt = createdAt;
        }
    }
}

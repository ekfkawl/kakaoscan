package com.kakaoscan.server.application.controller.api;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.dto.response.SearchHistories;
import com.kakaoscan.server.application.service.SearchHistoryService;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "SearchHistory", description = "SearchHistory API")
public class SearchHistoryController extends ApiEndpointPrefix {
    private final SearchHistoryService searchHistoryService;

    @GetMapping("/search-histories")
    @Operation(summary = "search history within N hours of the createdAt")
    public ResponseEntity<ApiResponse<SearchHistories>> findSearchHistories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        SearchHistories searchHistories = searchHistoryService.findUserSearchHistories(userDetails.getEmail());

        return new ResponseEntity<>(ApiResponse.success(searchHistories), HttpStatus.OK);
    }
}

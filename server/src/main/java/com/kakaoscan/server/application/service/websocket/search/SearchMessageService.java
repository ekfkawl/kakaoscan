package com.kakaoscan.server.application.service.websocket.search;

import com.kakaoscan.server.application.service.PointService;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.common.validation.ValidationPatterns;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.search.entity.NewPhoneNumber;
import com.kakaoscan.server.domain.search.model.NewNumberSearch;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.domain.search.repository.NewPhoneNumberRepository;
import com.kakaoscan.server.infrastructure.exception.UserNotVerifiedException;
import com.kakaoscan.server.infrastructure.websocket.queue.SearchInMemoryQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.CONCURRENT_MODIFICATION_POINTS;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_NOT_PHONE_NUMBER_FORMAT;

@Service
@RequiredArgsConstructor
public class SearchMessageService {
    private final StompMessageDispatcher messageDispatcher;
    private final PointService pointService;
    private final SearchInMemoryQueue queue;
    private final NewPhoneNumberRepository newPhoneNumberRepository;

    public SearchMessage createSearchMessage(Principal principal, SearchMessage.OriginMessage originMessage) {
        if (principal != null) {
            final String phoneNumber = originMessage.getContent().trim().replace("-", "");
            if (phoneNumber.matches(ValidationPatterns.PHONE_NUMBER)) {
                return new SearchMessage(principal.getName(), phoneNumber);
            }else {
                messageDispatcher.sendToUser(new SearchMessage(principal.getName(), SEARCH_NOT_PHONE_NUMBER_FORMAT, false));
                throw new IllegalArgumentException("message content is not a phone number format: " + phoneNumber);
            }
        }else {
            throw new UserNotVerifiedException("websocket principal is empty");
        }
    }

    public Optional<SearchMessage> findSearchMessage(String email) {
        Iterator<SearchMessage> iterator = queue.iterator();
        while (iterator.hasNext()) {
            SearchMessage next = iterator.next();
            if (next.getEmail().equals(email)) {
                return Optional.of(next);
            }
        }
        return Optional.empty();
    }

    public boolean validatePoints(SearchMessage message) {
        try {
            int points = pointService.getAndCachePoints(message.getEmail());

            SearchCost searchCost = pointService.getAndCacheTargetSearchCost(message.getEmail(), message.getContent());
            return points >= searchCost.getCost();

        } catch (ConcurrentModificationException e) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), CONCURRENT_MODIFICATION_POINTS, false));
            return false;
        }
    }

    public boolean canAttemptNumberSearch(SearchMessage message) {
        List<NewPhoneNumber> newPhoneNumbers = newPhoneNumberRepository.findNewPhoneNumbersByDate(LocalDate.now());
        if (newPhoneNumbers.size() < 50) {
            return true;
        }

        Optional<NewPhoneNumber> targetPhoneNumberOptional = newPhoneNumberRepository.findByTargetPhoneNumber(message.getContent());
        return targetPhoneNumberOptional.isPresent();
    }
}

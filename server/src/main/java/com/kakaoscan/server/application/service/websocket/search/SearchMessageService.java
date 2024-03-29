package com.kakaoscan.server.application.service.websocket.search;

import com.kakaoscan.server.application.service.PointService;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.common.validation.ValidationPatterns;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.exception.UserNotVerifiedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ConcurrentModificationException;

import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.CONCURRENT_MODIFICATION_POINTS;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.SEARCH_INVALID_PHONE_NUMBER;

@Service
@RequiredArgsConstructor
public class SearchMessageService {
    private final StompMessageDispatcher messageDispatcher;
    private final PointService pointService;

    @Value("${search.profile.cost.origin}")
    private int costOrigin;

    public SearchMessage createSearchMessage(Principal principal, SearchMessage.OriginMessage originMessage) {
        if (principal != null) {
            final String phoneNumber = originMessage.getContent().trim().replace("-", "");
            if (phoneNumber.matches(ValidationPatterns.PHONE_NUMBER)) {
                return new SearchMessage(principal.getName(), phoneNumber, true);
            }else {
                messageDispatcher.sendToUser(new SearchMessage(principal.getName(), SEARCH_INVALID_PHONE_NUMBER, false));
                throw new IllegalArgumentException("message content is not a phone number format");
            }
        }else {
            throw new UserNotVerifiedException("websocket principal is empty");
        }
    }

    public boolean validatePoints(SearchMessage message) {
        try {
            int points = pointService.getPointsFromCache(message.getEmail());
            return points >= 500;

        } catch (ConcurrentModificationException e) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), CONCURRENT_MODIFICATION_POINTS, false));
            return false;
        }
    }
}

package com.kakaoscan.server.application.service.websocket.search;

import com.kakaoscan.server.application.port.PointPort;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.exception.UserNotVerifiedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ConcurrentModificationException;

import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.CONCURRENT_MODIFICATION_POINTS;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.NOT_ENOUGH_POINTS;

@Service
@RequiredArgsConstructor
public class SearchMessageService {
    private final StompMessageDispatcher messageDispatcher;
    private final PointPort pointPort;

    @Value("${search.cost}")
    private int searchCost;

    public SearchMessage createSearchMessage(Principal principal, SearchMessage.OriginMessage originMessage) {
        if (principal != null) {
            final String phoneNumber = originMessage.getContent().trim().replace("-", "");
            if (phoneNumber.length() == 11 && phoneNumber.matches("\\d+")) {
                return new SearchMessage(principal.getName(), phoneNumber);
            }else {
                throw new IllegalArgumentException("message content is not a phone number format");
            }
        }else {
            throw new UserNotVerifiedException("websocket principal is empty");
        }
    }

    public boolean validatePoints(SearchMessage message) {
        try {
            int points = pointPort.getPointsFromCache(message.getEmail());
            return points >= searchCost;

        } catch (ConcurrentModificationException e) {
            messageDispatcher.sendToUser(new SearchMessage(message.getEmail(), CONCURRENT_MODIFICATION_POINTS, false, false));
            return false;
        }
    }
}

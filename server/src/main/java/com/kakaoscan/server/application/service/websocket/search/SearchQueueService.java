package com.kakaoscan.server.application.service.websocket.search;

import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.infrastructure.websocket.queue.SearchInMemoryQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchQueueService {
    private final SearchInMemoryQueue queue;

    public Optional<SearchMessage> enqueueAndPeekNext(SearchMessage searchMessage) {
        if (!queue.contains(searchMessage.getEmail())) {
            queue.add(searchMessage);
        }
        Optional<SearchMessage> optionalMessage = queue.size() == 0 ? Optional.empty() : Optional.of(queue.iterator().next());
        optionalMessage.ifPresent(m -> {
            if (m.getEventStartedAt() == null) {
                m.createEventStartedAt();
            }
        });

        return optionalMessage;
    }

    public Optional<SearchMessage> findMessage(String email) {
        Iterator<SearchMessage> iterator = queue.iterator();
        while (iterator.hasNext()) {
            SearchMessage next = iterator.next();
            if (next.getEmail().equals(email)) {
                return Optional.of(next);
            }
        }
        return Optional.empty();
    }
}

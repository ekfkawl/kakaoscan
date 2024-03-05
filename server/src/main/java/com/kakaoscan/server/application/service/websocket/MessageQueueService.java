package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.domain.search.model.ProfileMessage;
import com.kakaoscan.server.infrastructure.websocket.queue.ProfileInMemoryQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageQueueService {
    private final ProfileInMemoryQueue queue;

    public Optional<ProfileMessage> enqueueAndPeekNext(ProfileMessage profileMessage) {
        if (!queue.contains(profileMessage.getEmail())) {
            queue.add(profileMessage);
        }
        Optional<ProfileMessage> optionalMessage = queue.size() == 0 ? Optional.empty() : Optional.of(queue.iterator().next());
        optionalMessage.ifPresent(m -> {
            if (m.getEventStartedAt() == null) {
                m.createEventStartedAt();
            }
        });

        return optionalMessage;
    }

    public Optional<ProfileMessage> findMessage(String email) {
        Iterator<ProfileMessage> iterator = queue.iterator();
        while (iterator.hasNext()) {
            ProfileMessage next = iterator.next();
            if (next.getEmail().equals(email)) {
                return Optional.of(next);
            }
        }
        return Optional.empty();
    }
}

package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.domain.search.queue.QueueAggregate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageQueueService {
    private final QueueAggregate queue;

    public Optional<Message> enqueueAndPeekNext(Message message) {
        if (!queue.contains(message.getEmail())) {
            queue.add(message);
        }
        Optional<Message> optionalMessage = queue.size() == 0 ? Optional.empty() : Optional.of(queue.iterator().next());
        optionalMessage.ifPresent(m -> {
            if (m.getEventStartedAt() == null) {
                m.createEventStartedAt();
            }
        });

        return optionalMessage;
    }

    public Optional<Message> findMessage(String email) {
        Iterator<Message> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Message next = iterator.next();
            if (next.getEmail().equals(email)) {
                return Optional.of(next);
            }
        }
        return Optional.empty();
    }
}

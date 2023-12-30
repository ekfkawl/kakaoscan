package com.kakaoscan.server.application.service.websocket;

import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.domain.search.queue.QueueAggregate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageQueueService {
    private final QueueAggregate queue;

    public Optional<Message> enqueueAndPeekNext(Message message) {
        if (!queue.contains(message.getEmail())) {
            queue.add(message);
        }
        return (queue.size() <= 0) ? Optional.empty() : Optional.of(queue.iterator().next());
    }
}

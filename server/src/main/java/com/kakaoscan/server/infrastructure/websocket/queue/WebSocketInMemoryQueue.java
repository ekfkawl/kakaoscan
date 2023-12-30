package com.kakaoscan.server.infrastructure.websocket.queue;

import com.kakaoscan.server.domain.search.model.Message;
import com.kakaoscan.server.domain.search.queue.QueueAggregate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class WebSocketInMemoryQueue implements QueueAggregate {

    private final ConcurrentSkipListSet<Message> set;

    public WebSocketInMemoryQueue() {
        this.set = new ConcurrentSkipListSet<>(Comparator.comparing(Message::getCreatedAt)
                .thenComparing(Message::getEmail));
    }

    @Override
    public void add(Message message) {
        set.add(message);
    }

    @Override
    public boolean contains(String id) {
        return set.stream().anyMatch(message -> id.equals(message.getEmail()));
    }

    @Override
    public boolean remove(String id) {
        return set.removeIf(message -> id.equals(message.getEmail()));
    }

    @Override
    public void update(String id, Message newMessage) {
        set.removeIf(message -> id.equals(message.getEmail()));
        set.add(newMessage);
    }

    @Override
    public Iterator<Message> iterator() {
        return set.iterator();
    }

    @Override
    public int size() {
        return set.size();
    }
}

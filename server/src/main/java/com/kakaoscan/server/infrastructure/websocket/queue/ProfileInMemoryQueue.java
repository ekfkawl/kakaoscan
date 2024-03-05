package com.kakaoscan.server.infrastructure.websocket.queue;

import com.kakaoscan.server.domain.search.model.ProfileMessage;
import com.kakaoscan.server.domain.search.queue.QueueAggregate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class ProfileInMemoryQueue implements QueueAggregate {

    private final ConcurrentSkipListSet<ProfileMessage> set;

    public ProfileInMemoryQueue() {
        this.set = new ConcurrentSkipListSet<>(Comparator.comparing(ProfileMessage::getCreatedAt)
                .thenComparing(ProfileMessage::getEmail));
    }

    @Override
    public void add(ProfileMessage profileMessage) {
        set.add(profileMessage);
    }

    @Override
    public boolean contains(String id) {
        return set.stream().anyMatch(message -> id.equals(message.getEmail()));
    }

    @Override
    public void remove(String id) {
        set.removeIf(message -> id.equals(message.getMessageId()));
    }

    @Override
    public void update(String id, ProfileMessage newProfileMessage) {
        set.removeIf(message -> id.equals(message.getMessageId()));
        set.add(newProfileMessage);
    }

    @Override
    public Iterator<ProfileMessage> iterator() {
        return set.iterator();
    }

    @Override
    public int size() {
        return set.size();
    }
}

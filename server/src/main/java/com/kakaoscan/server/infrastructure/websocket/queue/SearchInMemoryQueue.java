package com.kakaoscan.server.infrastructure.websocket.queue;

import com.kakaoscan.server.domain.search.model.SearchMessage;
import com.kakaoscan.server.domain.search.queue.QueueAggregate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class SearchInMemoryQueue implements QueueAggregate {

    private final ConcurrentSkipListSet<SearchMessage> set;

    public SearchInMemoryQueue() {
        this.set = new ConcurrentSkipListSet<>(Comparator.comparing(SearchMessage::getCreatedAt)
                .thenComparing(SearchMessage::getEmail));
    }

    @Override
    public void add(SearchMessage searchMessage) {
        set.add(searchMessage);
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
    public void update(String id, SearchMessage newSearchMessage) {
        set.removeIf(message -> id.equals(message.getMessageId()));
        set.add(newSearchMessage);
    }

    @Override
    public Iterator<SearchMessage> iterator() {
        return set.iterator();
    }

    @Override
    public int size() {
        return set.size();
    }
}

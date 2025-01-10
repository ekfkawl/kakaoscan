package com.kakaoscan.server.domain.search.queue;

import com.kakaoscan.server.domain.search.model.SearchMessage;

import java.util.Iterator;
import java.util.Optional;

public interface QueueAggregate {
    void add(SearchMessage searchMessage);
    Optional<SearchMessage> peek();
    boolean contains(String id);
    void remove(String id);
    void update(String id, SearchMessage newSearchMessage);
    Iterator<SearchMessage> iterator();
    int size();
}

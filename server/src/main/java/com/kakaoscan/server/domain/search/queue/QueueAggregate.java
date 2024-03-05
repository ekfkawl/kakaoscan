package com.kakaoscan.server.domain.search.queue;

import com.kakaoscan.server.domain.search.model.SearchMessage;

import java.util.Iterator;

public interface QueueAggregate {
    void add(SearchMessage searchMessage);
    boolean contains(String id);
    void remove(String id);
    void update(String id, SearchMessage newSearchMessage);
    Iterator<SearchMessage> iterator();
    int size();
}

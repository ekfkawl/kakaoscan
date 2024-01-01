package com.kakaoscan.server.domain.search.queue;

import com.kakaoscan.server.domain.search.model.Message;

import java.util.Iterator;

public interface QueueAggregate {
    void add(Message message);
    boolean contains(String id);
    void remove(String id);
    void update(String id, Message newMessage);
    Iterator<Message> iterator();
    int size();
}

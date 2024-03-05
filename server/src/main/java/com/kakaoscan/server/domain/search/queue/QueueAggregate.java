package com.kakaoscan.server.domain.search.queue;

import com.kakaoscan.server.domain.search.model.ProfileMessage;

import java.util.Iterator;

public interface QueueAggregate {
    void add(ProfileMessage profileMessage);
    boolean contains(String id);
    void remove(String id);
    void update(String id, ProfileMessage newProfileMessage);
    Iterator<ProfileMessage> iterator();
    int size();
}

package com.kakaoscan.server.application.port;

import java.util.concurrent.TimeUnit;

public interface CacheStorePort<V> {
    void put(String key, V value);
    void put(String key, V value, long timeout, TimeUnit unit);
    V get(String key, Class<V> type);
    boolean containsKey(String key, Class<V> type);
    void deleteKey(String key, Class<V> type);
}

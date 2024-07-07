package com.kakaoscan.server.infrastructure.redis.utils;

import com.kakaoscan.server.application.port.CacheStorePort;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RedisCacheUtil {
    public static <T> T getFromCacheOrSupplier(CacheStorePort<T> cacheStorePort, String key, Class<T> type, Supplier<T> absentSupplier, long timeout, TimeUnit unit) {
        T value = cacheStorePort.get(key, type);
        if (value != null) {
            return value;
        }

        value = absentSupplier.get();
        cacheStorePort.put(key, value, timeout, unit);

        return value;
    }
}

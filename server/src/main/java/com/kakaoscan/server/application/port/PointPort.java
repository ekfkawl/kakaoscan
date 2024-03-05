package com.kakaoscan.server.application.port;

public interface PointPort {
    void cachePoints(String userId);

    int getPointsFromCache(String userId);

    boolean deductPoints(String userId, int value);
}

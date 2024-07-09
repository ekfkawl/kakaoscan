package com.kakaoscan.server.domain.point.model;

public interface PointBalanceObserver {
    void update(String userId, int points);
}

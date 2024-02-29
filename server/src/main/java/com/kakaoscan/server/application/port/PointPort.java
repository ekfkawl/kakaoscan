package com.kakaoscan.server.application.port;

public interface PointPort {
    boolean deductPoints(String userId, int value);
}

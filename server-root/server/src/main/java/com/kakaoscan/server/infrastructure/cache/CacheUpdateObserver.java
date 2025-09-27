package com.kakaoscan.server.infrastructure.cache;

import com.kakaoscan.server.application.port.CacheStorePort;
import com.kakaoscan.server.application.service.websocket.StompMessageDispatcher;
import com.kakaoscan.server.domain.point.model.PointBalanceObserver;
import com.kakaoscan.server.domain.point.model.PointMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;

import static com.kakaoscan.server.infrastructure.constants.RedisKeyPrefixes.POINT_CACHE_KEY_PREFIX;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.LOADING_POINTS_BALANCE;
import static com.kakaoscan.server.infrastructure.constants.ResponseMessages.LOADING_POINTS_BALANCE_ERROR;

@Component
@RequiredArgsConstructor
public class CacheUpdateObserver implements PointBalanceObserver {
    private final CacheStorePort<Integer> integerCacheStorePort;
    private final StompMessageDispatcher messageDispatcher;

    @Override
    public void update(String userId, int points) {
        integerCacheStorePort.put(POINT_CACHE_KEY_PREFIX + userId.toLowerCase(), points, 3, TimeUnit.MINUTES);

        try {
            messageDispatcher.sendToUser(new PointMessage(userId, points));
        } catch (ConcurrentModificationException e) {
            messageDispatcher.sendToUser(new PointMessage(userId, -1, LOADING_POINTS_BALANCE));
        } catch (Exception e) {
            messageDispatcher.sendToUser(new PointMessage(userId, -1, LOADING_POINTS_BALANCE_ERROR));
        }
    }
}
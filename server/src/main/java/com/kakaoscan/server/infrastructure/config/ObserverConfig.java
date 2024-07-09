package com.kakaoscan.server.infrastructure.config;

import com.kakaoscan.server.domain.point.model.PointBalanceObservable;
import com.kakaoscan.server.infrastructure.cache.CacheUpdateObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ObserverConfig {
    private final PointBalanceObservable pointBalanceObservable;
    private final CacheUpdateObserver cacheUpdateObserver;

    @PostConstruct
    public void init() {
        pointBalanceObservable.addObserver(cacheUpdateObserver);
    }
}

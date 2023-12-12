package com.kakaoscan.server.infrastructure.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Object lock = new Object();
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket createBucket() {
        long capacity = 10;
        Refill refill = Refill.greedy(capacity, Duration.ofHours(1));
        Bandwidth limit = Bandwidth.classic(capacity, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public Bucket resolveBucket(String remoteAddress) {
        return buckets.computeIfAbsent(remoteAddress, v -> createBucket());
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void resetBuckets() {
        synchronized (lock) {
            buckets.clear();
        }
    }
}

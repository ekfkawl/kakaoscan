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

    private Bucket createBucket(long capacity, Duration refillDuration) {
        Refill refill = Refill.greedy(capacity, refillDuration);
        Bandwidth limit = Bandwidth.classic(capacity, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public Bucket resolveBucket(String key, long capacity, Duration refillDuration) {
        return buckets.computeIfAbsent(key, v -> createBucket(capacity, refillDuration));
    }

    public boolean isBucketFull(String key) {
        Bucket bucket = buckets.get(key);
        if (bucket == null) {
            return false;
        }
        return bucket.getAvailableTokens() == 0;
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    private void resetBuckets() {
        synchronized (lock) {
            buckets.clear();
        }
    }
}

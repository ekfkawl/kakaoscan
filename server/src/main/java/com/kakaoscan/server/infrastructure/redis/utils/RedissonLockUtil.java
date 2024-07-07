package com.kakaoscan.server.infrastructure.redis.utils;

import org.redisson.api.RLock;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.TimeUnit;

import static com.kakaoscan.server.infrastructure.config.RedissonConfig.LOCK_LEASE_TIME;
import static com.kakaoscan.server.infrastructure.config.RedissonConfig.LOCK_WAIT_TIME;

public class RedissonLockUtil {

    public static boolean withLock(RLock lock, Runnable runnable) {
        try {
            if (!lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                return false;
            }

            try {
                runnable.run();
            } finally {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        lock.unlock();
                    }
                });
            }

            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Lock acquisition interrupted", e);
        }
    }
}

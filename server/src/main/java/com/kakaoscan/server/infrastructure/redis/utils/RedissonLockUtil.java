package com.kakaoscan.server.infrastructure.redis.utils;

import org.redisson.api.RLock;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.kakaoscan.server.infrastructure.config.RedissonConfig.LOCK_LEASE_TIME;
import static com.kakaoscan.server.infrastructure.config.RedissonConfig.LOCK_WAIT_TIME;

public class RedissonLockUtil {

    public static boolean withLock(RLock lock, Runnable runnable) {
        try {
            if (!lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                return false;
            }

            AtomicReference<Exception> exception = new AtomicReference<>(null);
            try {
                runnable.run();
            } catch (Exception e) {
                exception.set(e);
            } finally {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        lock.unlock();
                        if (exception.get() != null) {
                            throw new RuntimeException(exception.get());
                        }
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

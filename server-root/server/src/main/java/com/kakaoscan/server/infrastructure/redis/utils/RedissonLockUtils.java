package com.kakaoscan.server.infrastructure.redis.utils;

import org.redisson.api.RLock;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.kakaoscan.server.infrastructure.config.RedissonConfig.LOCK_LEASE_TIME;
import static com.kakaoscan.server.infrastructure.config.RedissonConfig.LOCK_WAIT_TIME;

public class RedissonLockUtils {

    public static boolean withLock(RLock lock, Runnable runnable) {
        boolean locked = false;
        try {
            locked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!locked) {
                return false;
            }

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        try {
                            if (lock.isHeldByCurrentThread()) {
                                lock.unlock();
                            }
                        } catch (Exception ignore) { }
                    }
                });
            }

            runnable.run();
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Lock acquisition interrupted", e);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                try {
                    if (locked && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                } catch (Exception ignore) { }
            }
        }
    }

}

package io.ekfkawl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionSupportUtils {
    public static <T> T handleException(String message, Exception exception) {
        log.error(message, exception);
        throw new RuntimeException(message, exception);
    }

    public static <T extends RuntimeException, R> R handleException(String message, Exception exception, Class<T> exceptionClass) {
        log.error(message, exception);
        try {
            throw exceptionClass.getConstructor(String.class, Throwable.class).newInstance(message, exception);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("failed to create exception type " + exceptionClass.getName(), e);
        }
    }
}

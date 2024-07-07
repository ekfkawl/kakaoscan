package com.kakaoscan.server.common.utils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ExceptionHandler {

    public static void handleException(String message, Exception exception) {
        log.error(message, exception);
        throw new RuntimeException(message, exception);
    }

    public static <T extends RuntimeException> void handleException(String message, Exception exception, Class<T> exceptionClass) {
        log.error(message, exception);
        try {
            throw exceptionClass.getConstructor(String.class, Throwable.class).newInstance(message, exception);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("failed to create exception type " + exceptionClass.getName(), e);
        }
    }
}
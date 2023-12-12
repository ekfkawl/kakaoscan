package com.kakaoscan.server.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExceptionUtils {
    private static final Logger log = LogManager.getLogger(ExceptionUtils.class);

    public static <E extends RuntimeException> E throwException(String message, Throwable cause, Class<E> exceptionClass) {
        try {
            log.error(message, cause);
            return exceptionClass.getConstructor(String.class, Throwable.class).newInstance(message, cause);
        } catch (Exception e) {
            throw new RuntimeException("error instantiating exception " + exceptionClass.getName(), e);
        }
    }

    public static <E extends RuntimeException> E throwException(String message, Class<E> exceptionClass) {
        log.error(message);
        try {
            return exceptionClass.getConstructor(String.class).newInstance(message);
        } catch (Exception e) {
            throw new RuntimeException("error instantiating exception " + exceptionClass.getName(), e);
        }
    }
}
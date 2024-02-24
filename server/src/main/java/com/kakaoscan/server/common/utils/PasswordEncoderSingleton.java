package com.kakaoscan.server.common.utils;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderSingleton {
    private static final PasswordEncoder INSTANCE = createDelegatingPasswordEncoder();

    private static PasswordEncoder createDelegatingPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public static PasswordEncoder getInstance() {
        return INSTANCE;
    }
}

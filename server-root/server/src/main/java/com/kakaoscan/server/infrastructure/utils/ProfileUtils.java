package com.kakaoscan.server.infrastructure.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileUtils {
    private final Environment env;

    public boolean isProd() {
        return env.acceptsProfiles(Profiles.of("prod"));
    }

    public boolean isDev() {
        return env.acceptsProfiles(Profiles.of("dev", "local"));
    }
}
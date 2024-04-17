package com.kakaoscan.server.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {
    private final Environment env;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    public static final int LOCK_WAIT_TIME = 10;
    public static final int LOCK_LEASE_TIME = 30;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String redisUrl = String.format("redis://%s:%s", redisHost, redisPort);
        config.useSingleServer()
                .setAddress(redisUrl);

        if (!Arrays.asList(env.getActiveProfiles()).contains("dev")) {
            config.useSingleServer().setPassword(System.getenv("REDIS_PASSWORD"));
        }

        return Redisson.create(config);
    }

}

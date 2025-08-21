package com.kakaoscan.server.infrastructure.config;

import com.kakaoscan.server.infrastructure.utils.ProfileUtils;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {
    private final ProfileUtils profileUtils;

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

        if (profileUtils.isProd()) {
            config.useSingleServer().setPassword(System.getenv("REDIS_PASSWORD"));
        }

        return Redisson.create(config);
    }

}

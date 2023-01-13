package com.kakaoscan.profile.domain.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-db.properties, classpath:application-dev.properties, classpath:application.properties"})
class AccessLimitServiceTest {

    @Autowired
    AccessLimitService accessLimitService;

    @Test
    void updateUseCount() {
        accessLimitService.updateUseCount(0);
    }
}
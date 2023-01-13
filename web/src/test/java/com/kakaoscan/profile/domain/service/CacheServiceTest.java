package com.kakaoscan.profile.domain.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-db.properties, classpath:application-dev.properties, classpath:application.properties"})
class CacheServiceTest {

    @Autowired
    CacheService cacheService;

    @Test
    void updatePhoneNumberCache() {
        cacheService.updatePhoneNumberCache("01012341234", false);
    }

    @Test
    void isEnabledPhoneNumber() {
        cacheService.isEnabledPhoneNumber("01012341234");
    }
}
package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.config.TestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestProperties.class)
class CacheServiceTest {

    @Autowired
    CacheService cacheService;

    @Test
    void updatePhoneNumberCache() {
        cacheService.updatePhoneNumberCache("01012341234");
    }

    @Test
    void isEnabledPhoneNumber() {
        cacheService.isEnabledPhoneNumber("01012341234");
    }
}
package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.config.TestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestProperties.class)
class AccessLimitServiceTest {

    @Autowired
    AccessLimitService accessLimitService;

    @Test
    void updateUseCount() {
        accessLimitService.updateUseCount(0);
    }
}
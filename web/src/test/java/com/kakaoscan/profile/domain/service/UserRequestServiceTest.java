package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.config.TestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

@SpringBootTest
@Import(TestProperties.class)
class UserRequestServiceTest {

    @Autowired
    UserRequestService userRequestService;

    @Test
    void updateUseCount() {
        userRequestService.updateUseCount("test@test.com", "aa7f43535f25ca70763fe1a296b0a789");
    }

    @Test
    void getUseCount() {
        long aa7f43535f25ca70763fe1a296b0a789 = userRequestService.getUseCount("test@test.com");
        System.out.println("test@test.com = " + aa7f43535f25ca70763fe1a296b0a789);
    }

    @Test
    void syncUserUseCount() {
        userRequestService.syncUserUseCount("127.0.0.1", LocalDate.now());
    }
}
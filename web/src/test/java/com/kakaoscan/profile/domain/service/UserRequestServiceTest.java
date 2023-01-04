package com.kakaoscan.profile.domain.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-db.properties, classpath:application-dev.properties, classpath:application.properties"})
class UserRequestServiceTest {

    @Autowired
    UserRequestService userRequestService;

    @Test
    void updateUseCount() {
        userRequestService.updateUseCount("aa7f43535f25ca70763fe1a296b0a789");
    }

    @Test
    void getUseCount() {
        long aa7f43535f25ca70763fe1a296b0a789 = userRequestService.getUseCount("aa7f43535f25ca70763fe1a296b0a789");
        System.out.println("aa7f43535f25ca70763fe1a296b0a789 = " + aa7f43535f25ca70763fe1a296b0a789);
    }
}
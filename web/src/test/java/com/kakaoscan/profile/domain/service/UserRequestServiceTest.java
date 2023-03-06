package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.config.TestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestProperties.class)
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
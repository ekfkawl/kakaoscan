package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.config.TestProperties;
import com.kakaoscan.profile.domain.model.EmailMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestProperties.class)
class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @Test
    public void send() {
        emailService.send(EmailMessage.builder()
            .to("mail.kakaoscan@gmail.com")
            .subject("test subject")
            .build(), "email");
    }

}
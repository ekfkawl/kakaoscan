package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.model.EmailMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-smtp.properties, classpath:application-dev.properties, classpath:application.properties"})
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
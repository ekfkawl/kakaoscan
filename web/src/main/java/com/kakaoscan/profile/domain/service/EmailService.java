package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.model.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailService implements SendMail {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void send(EmailMessage emailMessage, String template) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(setContext(template), true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("MessagingException", e);
            throw new RuntimeException(e);
        }
    }

    public String setContext(String template) {
        Context context = new Context();
//        context.setVariable("", "");
        return templateEngine.process(template, context);
    }
}

package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.port.EmailPort;
import com.kakaoscan.server.infrastructure.email.template.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.kakaoscan.server.common.utils.ExceptionHandler.handleException;

@Service
@RequiredArgsConstructor
public class EmailAdapter implements EmailPort {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    private static final String ADDRESS = "mail.kakaoscan@gmail.com";
    private static final String PERSONAL = "카카오스캔";

    @Override
    public <T extends EmailTemplate> void send(T emailTemplate, Map<String, Object> variables) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailTemplate.getReceiver());
            mimeMessageHelper.setSubject(emailTemplate.getSubject());

            String content = setContext(emailTemplate.getHtml(), variables);
            mimeMessageHelper.setText(content, true);

            mimeMessageHelper.setFrom(new InternetAddress(ADDRESS, PERSONAL));
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            handleException("error sending email", e);
        }
    }

    public String setContext(String template) {
        Context context = new Context();
        return templateEngine.process(template, context);
    }

    public String setContext(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}

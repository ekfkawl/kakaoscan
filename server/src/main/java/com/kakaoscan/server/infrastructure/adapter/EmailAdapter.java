package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.dto.EmailTemplate;
import com.kakaoscan.server.application.dto.VerificationEmail;
import com.kakaoscan.server.application.port.EmailPort;
import com.kakaoscan.server.infrastructure.exception.EmailSendingException;
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
import java.util.HashMap;
import java.util.Map;

import static com.kakaoscan.server.common.utils.ExceptionUtils.throwException;

@Service
@RequiredArgsConstructor
public class EmailAdapter implements EmailPort {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    private static final String ADDRESS = "mail.kakaoscan@gmail.com";
    private static final String PERSONAL = "카카오스캔";

    @Override
    public <T extends EmailTemplate> void send(T emailTemplate) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailTemplate.getReceiver());
            mimeMessageHelper.setSubject(emailTemplate.getSubject());

            Map<String, Object> variables = new HashMap<>();
            if (emailTemplate instanceof VerificationEmail verificationEmail) {
                variables.put("verificationLink", verificationEmail.getVerificationLink());
            }
            String content = setContext(emailTemplate.getHtml(), variables);
            mimeMessageHelper.setText(content, true);

            mimeMessageHelper.setFrom(new InternetAddress(ADDRESS, PERSONAL));
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throwException("error sending email", e, EmailSendingException.class);
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

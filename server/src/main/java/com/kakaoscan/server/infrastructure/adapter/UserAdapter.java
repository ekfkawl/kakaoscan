package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.dto.request.RegisterRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.exception.AlreadyRegisteredException;
import com.kakaoscan.server.application.port.UserPort;
import com.kakaoscan.server.application.service.UserService;
import com.kakaoscan.server.domain.events.model.VerificationEmailEvent;
import com.kakaoscan.server.domain.user.entity.EmailVerificationToken;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.EmailTokenRepository;
import com.kakaoscan.server.infrastructure.email.types.VerificationEmail;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.kakaoscan.server.infrastructure.redis.enums.Topics.OTHER_EVENT_TOPIC;

@Service
@RequiredArgsConstructor
public class UserAdapter implements UserPort {
    private final UserService userService;
    private final EmailTokenRepository emailTokenRepository;
    private final EventPublisher eventPublisher;

    private static final String ALREADY_VERIFIED_EMAIL = "이미 인증된 이메일입니다.";
    private static final String TOKEN_DOES_NOT_EXIST = "존재하지 않는 토큰입니다.";

    @Value("${verify.prefix}")
    private String verifyPrefix;

    @Transactional
    @Override
    public ApiResponse<Void> register(RegisterRequest registerRequest) {
        User newUser;
        try {
            newUser = userService.registerUser(registerRequest.getEmail(), registerRequest.getPassword());
        } catch (AlreadyRegisteredException e) {
            return ApiResponse.failure(e.getMessage());
        }

        EmailVerificationToken verificationToken = createVerificationToken(newUser);

        VerificationEmail verificationEmail = new VerificationEmail(registerRequest.getEmail(), verifyPrefix + verificationToken.getToken());
        VerificationEmailEvent event = new VerificationEmailEvent(verificationEmail);
        eventPublisher.publish(OTHER_EVENT_TOPIC.getTopic(), event);

        return ApiResponse.success();
    }

    @Override
    public EmailVerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(user, token);
        emailTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Transactional
    @Override
    public ApiResponse<Void> consumeVerificationToken(String verificationToken) {
        Optional<EmailVerificationToken> tokenOptional = emailTokenRepository.findByToken(verificationToken);
        if (tokenOptional.isPresent()) {
            EmailVerificationToken token = tokenOptional.get();

            if (token.getUser().isEmailVerified()) {
                return ApiResponse.failure(ALREADY_VERIFIED_EMAIL);
            }

            token.getUser().verifyEmail();
            return ApiResponse.success();
        }else {
            return ApiResponse.failure(TOKEN_DOES_NOT_EXIST);
        }
    }
}

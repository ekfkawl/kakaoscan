package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.dto.request.RegisterRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.exception.AlreadyRegisteredException;
import com.kakaoscan.server.application.port.UserPort;
import com.kakaoscan.server.application.service.UserService;
import com.kakaoscan.server.domain.events.model.VerificationEmailEvent;
import com.kakaoscan.server.domain.user.model.EmailVerificationToken;
import com.kakaoscan.server.domain.user.model.User;
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

    private static final String USER_REGISTRATION_SUCCESS = "이메일로 보내드린 인증 링크를 클릭하시면 가입이 완료됩니다.";
    private static final String ALREADY_VERIFIED_EMAIL = "이미 인증된 이메일입니다.";
    private static final String SUCCESS_VERIFIED_EMAIL = "이메일 인증이 완료되었습니다.";
    private static final String TOKEN_DOES_NOT_EXIST = "존재하지 않는 토큰입니다.";

    @Value("${verify.prefix}")
    private String verifyPrefix;

    @Transactional
    @Override
    public ApiResponse register(RegisterRequest registerRequest) {
        User newUser;
        try {
            newUser = userService.registerUser(registerRequest.getEmail(), registerRequest.getPassword());
        } catch (AlreadyRegisteredException e) {
            return new ApiResponse(false, e.getMessage());
        }

        EmailVerificationToken verificationToken = createVerificationToken(newUser);

        VerificationEmail verificationEmail = new VerificationEmail(registerRequest.getEmail(), verifyPrefix + verificationToken.getToken());
        VerificationEmailEvent event = new VerificationEmailEvent(verificationEmail);
        eventPublisher.publish(OTHER_EVENT_TOPIC.getTopic(), event);

        return new ApiResponse(true, USER_REGISTRATION_SUCCESS, true);
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
    public ApiResponse consumeVerificationToken(String verificationToken) {
        Optional<EmailVerificationToken> tokenOptional = emailTokenRepository.findByToken(verificationToken);
        if (tokenOptional.isPresent()) {
            EmailVerificationToken token = tokenOptional.get();

            if (token.getUser().isEmailVerified()) {
                return new ApiResponse(false, ALREADY_VERIFIED_EMAIL);
            }

            token.getUser().verifyEmail();
            return new ApiResponse(true, SUCCESS_VERIFIED_EMAIL);
        }else {
            return new ApiResponse(false, TOKEN_DOES_NOT_EXIST);
        }
    }
}

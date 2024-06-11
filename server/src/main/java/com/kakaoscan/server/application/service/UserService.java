package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.request.RegisterRequest;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.common.utils.PasswordEncoderSingleton;
import com.kakaoscan.server.domain.events.model.VerificationEmailEvent;
import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.product.repository.ProductTransactionRepository;
import com.kakaoscan.server.domain.search.repository.SearchHistoryRepository;
import com.kakaoscan.server.domain.user.entity.EmailVerificationToken;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.enums.Role;
import com.kakaoscan.server.domain.user.repository.EmailTokenRepository;
import com.kakaoscan.server.domain.user.repository.UserRepository;
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
public class UserService {
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final ProductTransactionRepository productTransactionRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final EventPublisher eventPublisher;

    private static final String ALREADY_REGISTERED_EMAIL = "이미 가입된 이메일입니다.";
    private static final String ALREADY_VERIFIED_EMAIL = "이미 인증된 이메일입니다.";
    private static final String TOKEN_DOES_NOT_EXIST = "존재하지 않는 토큰입니다.";

    @Value("${verify.prefix}")
    private String verifyPrefix;

    @Transactional
    public ApiResponse<Void> register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail().toLowerCase());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.isEmailVerified()) {
                return ApiResponse.failure(ALREADY_REGISTERED_EMAIL);
            }else {
                user.setDeleted(false);
                user.setPassword(PasswordEncoderSingleton.getInstance().encode(request.getPassword()));

                sendVerificationEmail(user);
                return ApiResponse.success();
            }
        }

        User newUser = User.builder()
                .email(request.getEmail().toLowerCase())
                .password(PasswordEncoderSingleton.getInstance().encode(request.getPassword()))
                .role(Role.USER)
                .authenticationType(AuthenticationType.LOCAL)
                .isDeleted(false)
                .build();

        newUser.initializePoint();
        userRepository.save(newUser);

        sendVerificationEmail(newUser);
        return ApiResponse.success();
    }

    private void sendVerificationEmail(User user) {
        EmailVerificationToken verificationToken = createVerificationToken(user);
        VerificationEmailEvent event = new VerificationEmailEvent(user.getEmail(), verifyPrefix + verificationToken.getToken());
        eventPublisher.publish(OTHER_EVENT_TOPIC.getTopic(), event);
    }

    @Transactional
    public User findOrRegisterOAuthUser(String email, AuthenticationType authenticationType) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = User.builder()
                            .email(email)
                            .password(null)
                            .role(Role.USER)
                            .authenticationType(authenticationType)
                            .isEmailVerified(true)
                            .build();

                    user.initializePoint();
                    return userRepository.save(user);
                });
    }

//    @Transactional
    public EmailVerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(user, token);
        emailTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Transactional
    public ApiResponse<Void> consumeVerificationToken(String verificationToken) {
        Optional<EmailVerificationToken> tokenOptional = emailTokenRepository.findByToken(verificationToken);
        if (tokenOptional.isPresent()) {
            EmailVerificationToken token = tokenOptional.get();

            if (token.getUser().isEmailVerified()) {
                return ApiResponse.failure(ALREADY_VERIFIED_EMAIL);
            }

            token.getUser().verifyEmail();
            return ApiResponse.success();
        } else {
            return ApiResponse.failure(TOKEN_DOES_NOT_EXIST);
        }
    }

    @Transactional
    public void changePassword(String userId, String password) {
        User user = userRepository.findByEmailOrThrow(userId);

        user.setPassword(PasswordEncoderSingleton.getInstance().encode(password));
    }

    @Transactional
    public void delete(String userId) {
        User user = userRepository.findByEmailOrThrow(userId);

        searchHistoryRepository.deleteByUser(user);

        PointWallet pointWallet = user.getPointWallet();
        if (pointWallet != null) {
            productTransactionRepository.deleteByWallet(pointWallet);
        }

        if (user.getAuthenticationType() == AuthenticationType.LOCAL) {
            user.setEmailVerified(false);
            user.setDeleted(true);
        }
    }
}

package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.exception.AlreadyRegisteredException;
import com.kakaoscan.server.common.utils.PasswordEncoderSingleton;
import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.enums.Role;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private static final String ALREADY_REGISTERED_EMAIL = "이미 가입된 이메일입니다.";

    @Transactional
    public User registerUser(String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            if (existingUser.get().isEmailVerified()) {
                throw new AlreadyRegisteredException(ALREADY_REGISTERED_EMAIL);
            }
            return existingUser.get();
        }else {
            User user = User.builder()
                    .email(email)
                    .password(PasswordEncoderSingleton.getInstance().encode(password))
                    .role(Role.USER)
                    .authenticationType(AuthenticationType.LOCAL)
                    .build();

            user.initializePoint();
            return userRepository.save(user);
        }
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
}

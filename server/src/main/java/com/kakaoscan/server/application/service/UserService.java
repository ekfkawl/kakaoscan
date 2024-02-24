package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.exception.AlreadyRegisteredException;
import com.kakaoscan.server.common.utils.PasswordEncoderSingleton;
import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.enums.Role;
import com.kakaoscan.server.domain.user.model.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private static final String ALREADY_REGISTERED_EMAIL = "이미 가입된 이메일입니다.";

    public User registerUser(String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            if (existingUser.get().isEmailVerified()) {
                throw new AlreadyRegisteredException(ALREADY_REGISTERED_EMAIL);
            }
            return existingUser.get();
        }else {
            return userRepository.save(User.builder()
                    .email(email)
                    .password(PasswordEncoderSingleton.getInstance().encode(password))
                    .role(Role.USER)
                    .authenticationType(AuthenticationType.LOCAL)
                    .build());
        }
    }

    public User findOrRegisterOAuthUser(String email, AuthenticationType authenticationType) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .password(null)
                        .role(Role.USER)
                        .authenticationType(authenticationType)
                        .isEmailVerified(true)
                        .build()));
    }
}

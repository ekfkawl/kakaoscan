package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.exception.AlreadyRegisteredException;
import com.kakaoscan.server.domain.user.AuthenticationType;
import com.kakaoscan.server.domain.user.Role;
import com.kakaoscan.server.domain.user.User;
import com.kakaoscan.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String ALREADY_REGISTERED_EMAIL = "이미 가입된 이메일입니다.";

    public User registerUser(String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            if (existingUser.get().isEmailVerified()) {
                throw new AlreadyRegisteredException(ALREADY_REGISTERED_EMAIL);
            }else {
                return existingUser.get();
            }
        }else {
            return userRepository.save(User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.USER)
                    .authenticationType(AuthenticationType.LOCAL)
                    .build());
        }
    }

}

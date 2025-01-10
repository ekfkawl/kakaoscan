package com.kakaoscan.server.application.domain.test;

import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.enums.Role;
import com.kakaoscan.server.domain.user.repository.UserRepository;

import java.util.ArrayList;

import static com.kakaoscan.server.infrastructure.config.TestConstant.TEST_USER_ID;

public class TestUserDataInitializer {
    private final UserRepository userRepository;

    public TestUserDataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser() {
        User user = User.builder()
                .id(0L)
                .email(TEST_USER_ID)
                .authenticationType(AuthenticationType.LOCAL)
                .isEmailVerified(true)
                .role(Role.USER)
                .searchHistories(new ArrayList<>())
                .build();

        user.initializePoint();
        user.getPointWallet().addBalance(1000);

        return userRepository.save(user);
    }
}

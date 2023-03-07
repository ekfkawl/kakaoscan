package com.kakaoscan.profile.domain.repository;

import com.kakaoscan.profile.domain.config.TestProperties;
import com.kakaoscan.profile.domain.entity.User;
import com.kakaoscan.profile.domain.entity.UserRequestUnlock;
import com.kakaoscan.profile.domain.respon.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(TestProperties.class)
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRequestUnlockRepository userRequestUnlockRepository;

    @Test
    void testUserMapping() {
        // given
        User user = User.builder()
                .email("test@test.com")
                .role(Role.USER)
                .modifyDt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        UserRequestUnlock requestUnlock = UserRequestUnlock.builder()
                .email("test@test.com")
                .message("Unlock request")
                .modifyDt(LocalDateTime.now())
                .build();
        userRequestUnlockRepository.save(requestUnlock);

        // when
        User user1 = userRepository.findById("test@test.com").orElse(null);
        UserRequestUnlock userRequestUnlock = userRequestUnlockRepository.findById("test@test.com").orElse(null);

        // then
        assertThat(user1.getRequestUnlock().getEmail()).isEqualTo(userRequestUnlock.getUser().getEmail());
    }

    @AfterEach
    public void delete() {
        userRepository.deleteById("test@test.com");
    }
}
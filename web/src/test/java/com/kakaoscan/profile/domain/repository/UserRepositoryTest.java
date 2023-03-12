package com.kakaoscan.profile.domain.repository;

import com.kakaoscan.profile.domain.config.TestProperties;
import com.kakaoscan.profile.domain.entity.User;
import com.kakaoscan.profile.domain.entity.UserHistory;
import com.kakaoscan.profile.domain.entity.UserRequestUnlock;
import com.kakaoscan.profile.domain.respon.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
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
    UserRepository userRepository;

    @Autowired
    UserRequestUnlockRepository userRequestUnlockRepository;

    @Autowired
    UserHistoryRepository userHistoryRepository;

    @Test
    @DisplayName("유저 entity 관계 맵핑 확인")
    void testUserMapping() {
        // given
        User user = User.builder()
                .email("test@test.com")
                .role(Role.USER)
                .modifyDt(LocalDateTime.now())
                .createDt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        UserRequestUnlock requestUnlock = UserRequestUnlock.builder()
                .email("test@test.com")
                .message("Unlock request")
                .modifyDt(LocalDateTime.now())
                .build();
        userRequestUnlockRepository.save(requestUnlock);

        for (int i = 1; i <= 2; i++) {
            UserHistory userHistory = UserHistory.builder()
                    .email("test@test.com")
                    .phoneNumber("01012345678")
                    .message(String.format("test%d.com", i))
                    .modifyDt(LocalDateTime.now())
                    .createDt(LocalDateTime.now())
                    .build();
            userHistoryRepository.save(userHistory);
        }

        // when
        User user1 = userRepository.findByEmail("test@test.com").orElse(null);
        UserRequestUnlock userRequestUnlock = userRequestUnlockRepository.findById("test@test.com").orElse(null);

        // then
        assertThat(user1.getRequestUnlock().getEmail()).isEqualTo(userRequestUnlock.getUser().getEmail());
        assertThat(user1.getHistoryList().size()).isEqualTo(2);

    }

    @AfterEach
    public void delete() {
        userRepository.deleteById("test@test.com");
    }
}
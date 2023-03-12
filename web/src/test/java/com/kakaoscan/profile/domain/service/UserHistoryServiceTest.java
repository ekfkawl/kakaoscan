package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.config.TestProperties;
import com.kakaoscan.profile.domain.entity.UserHistory;
import com.kakaoscan.profile.domain.repository.UserHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestProperties.class)
class UserHistoryServiceTest {

    @Autowired
    UserHistoryService userHistoryService;

    @Autowired
    UserHistoryRepository userHistoryRepository;

    @BeforeEach
    public void setUp() {
        UserHistory userHistory = UserHistory.builder()
                .email("test@test.com")
                .phoneNumber("01012345678")
                .message("test message")
                .modifyDt(LocalDateTime.now())
                .createDt(LocalDateTime.now().minusDays(7))
                .build();
        userHistoryRepository.save(userHistory);
    }

    @Test
    @DisplayName("7일 지난 데이터 삭제")
    public void deleteOldHistoryTest() {
        userHistoryService.deleteOldHistory();

        Optional<List<UserHistory>> optionalUserHistories = userHistoryRepository.findByCreateDtBefore(LocalDateTime.now().minusDays(7));
        assertThat(optionalUserHistories.isPresent()).isTrue();
    }

    @AfterEach
    public void tearDown() {
        Optional<List<UserHistory>> optionalUserHistories = userHistoryRepository.findByCreateDtBefore(LocalDateTime.now().minusDays(7));
        optionalUserHistories.ifPresent(userHistoryRepository::deleteAll);
    }
}
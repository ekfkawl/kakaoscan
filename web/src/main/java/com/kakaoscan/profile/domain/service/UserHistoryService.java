package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.entity.UserHistory;
import com.kakaoscan.profile.domain.repository.UserHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserHistoryService {

    private final UserHistoryRepository userHistoryRepository;

    @Transactional
    public void updateHistory(String email, String phoneNumber, String message) {

        Optional<UserHistory> optionalUserHistory = userHistoryRepository.findByEmailAndPhoneNumber(email, phoneNumber);
        UserHistory userHistory = optionalUserHistory.orElseGet(UserHistory::new);

        userHistory.update(email, phoneNumber, message);
        userHistoryRepository.save(userHistory);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void deleteOldHistory() {
        Optional<List<UserHistory>> optionalUserHistories = userHistoryRepository.findByCreateDtBefore(LocalDateTime.now().minusDays(7));
        optionalUserHistories.ifPresent(userHistoryRepository::deleteAll);
    }
}

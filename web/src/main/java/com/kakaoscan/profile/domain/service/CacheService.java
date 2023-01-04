package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.entity.Cache;
import com.kakaoscan.profile.domain.repository.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheRepository cacheRepository;

    /**
     * 하루 이상 지난 전화번호 유효 상태 업데이트
     * @param phoneNumber
     * @param enabled
     * @return
     */
    @Transactional
    public boolean updatePhoneNumberCache(String phoneNumber, boolean enabled) {
        boolean isSave = true;

        Optional<Cache> cache = cacheRepository.findById(phoneNumber);
        if (cache.isPresent()) {
            long d = ChronoUnit.DAYS.between(cache.get().getModifyDt(), LocalDateTime.now());
            isSave = d > 0;
        }

        if (isSave) {
            cacheRepository.save(Cache.builder()
                    .phoneNumber(phoneNumber)
                    .enabled(enabled)
                    .build());
        }

        return isSave;
    }

    /**
     * 유효한 전화번호인지 확인
     * @param phoneNumber
     * @return
     */
    public boolean isEnabledPhoneNumber(String phoneNumber) {
        Optional<Cache> cache = cacheRepository.findById(phoneNumber);
        if (cache.isPresent()) {
            long d = ChronoUnit.DAYS.between(cache.get().getModifyDt(), LocalDateTime.now());

            return d > 0;
        }else {
            return true;
        }
    }
}

package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.entity.AccessLimit;
import com.kakaoscan.profile.domain.model.UseCount;
import com.kakaoscan.profile.domain.repository.AccessLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessLimitService {

    private final AccessLimitRepository accessLimitRepository;

    @Transactional(readOnly = true)
    public UseCount getUseCount() {
        long res[] = new long[1];

        Optional<AccessLimit> accessLimit = accessLimitRepository.findById(LocalDate.now());
        res[0] = accessLimit.map(AccessLimit::getUseCount).orElse(0L);
//        res[1] = accessLimit.map(AccessLimit::getUseCount2).orElse(0L);

        return new UseCount(res, res[0]);
    }

    @Transactional
    public boolean updateUseCount(int serverIndex) {
        LocalDate now = LocalDate.now();
        long count = 1;
        long count2 = 0;
        long count3 = 0;

        Optional<AccessLimit> accessLimit = accessLimitRepository.findLockById(now);
        if (accessLimit.isPresent()) {

            count = accessLimit.get().getUseCount();
            count2 = accessLimit.get().getUseCount2();
            count3 = accessLimit.get().getUseCount3();

            switch (serverIndex) {
                case 0:
                    count++;
                    break;
                case 1:
                    count2++;
                    break;
                case 2:
                    count3++;
                    break;
            }
        }

        accessLimitRepository.save(AccessLimit.builder()
                .date(LocalDate.now())
                .useCount(count)
                .useCount2(count2)
                .useCount3(count3)
                .build());

        return true;
    }
}

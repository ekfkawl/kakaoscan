package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.entity.UserRequest;
import com.kakaoscan.profile.domain.repository.UserRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRequestService {

    private final UserRequestRepository userRequestRepository;

    public long getUseCount(String remoteAddress) {
        Optional<UserRequest> userRequest = userRequestRepository.findByRemoteAddressAndLastUseDt(remoteAddress, LocalDate.now());

        return userRequest.map(UserRequest::getUseCount).orElse(0L);
    }

    @Transactional
    public boolean updateUseCount(String remoteAddress) {

        long count = 1;

        Optional<UserRequest> userRequest = userRequestRepository.findLockById(remoteAddress);
        if (userRequest.isPresent()) {

            if (LocalDate.now().equals(userRequest.get().getLastUseDt())) {
                count = userRequest.get().getUseCount() + 1;
            }
        }
        userRequestRepository.save(UserRequest.builder()
                .remoteAddress(remoteAddress)
                .useCount(count)
                .build());

        return true;

    }
}

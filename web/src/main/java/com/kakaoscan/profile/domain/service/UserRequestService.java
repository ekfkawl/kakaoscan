package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.entity.UserRequest;
import com.kakaoscan.profile.domain.repository.UserRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRequestService {

    private final UserRequestRepository userRequestRepository;

    @Transactional
    public long getTodayUseCount(String email) {
        Optional<UserRequest> optionalUserRequest = userRequestRepository.findLockById(email);
        if (optionalUserRequest.isPresent()) {
            UserRequest userRequest = optionalUserRequest.get();

            if (LocalDate.now().equals(userRequest.getLastUseDt())) {
                return userRequest.getUseCount();
            }else {
                return 0;
            }
        }

        return -1;
    }

    @Transactional
    public boolean updateUseCount(String email, String remoteAddress) {
        long count = 1;

        Optional<UserRequest> userRequest = userRequestRepository.findLockById(email);
        if (userRequest.isPresent()) {

            if (LocalDate.now().equals(userRequest.get().getLastUseDt())) {
                count = userRequest.get().getUseCount() + 1;
            }
        }
        userRequestRepository.save(UserRequest.builder()
                .email(email)
                .remoteAddress(remoteAddress)
                .useCount(count)
                .lastUseDt(LocalDate.now())
                .build());

        return true;
    }

    @Transactional
    public void initUseCount(String email, String remoteAddress) {
        userRequestRepository.save(UserRequest.builder()
                .email(email)
                .remoteAddress(remoteAddress)
                .useCount(0)
                .lastUseDt(LocalDate.now())
                .build());
    }

    @Transactional
    public boolean syncUserUseCount(String remoteAddress, LocalDate localDate) {

        List<UserRequest> userRequests = userRequestRepository.findByRemoteAddressAndLastUseDt(remoteAddress, localDate);

        if (userRequests.size() > 1) {
            Collections.sort(userRequests, new Comparator<UserRequest>() {
                @Override
                public int compare(UserRequest o1, UserRequest o2) {
                    return (int) (o2.getUseCount() - o1.getUseCount());
                }
            });

            long useCount = userRequests.get(0).getUseCount();
            for (UserRequest userRequest : userRequests) {
                userRequest.setUseCount(useCount);
            }

            return true;
        }

        return false;
    }
}

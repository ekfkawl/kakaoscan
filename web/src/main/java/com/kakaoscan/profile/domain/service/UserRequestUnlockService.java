package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.entity.UserRequestUnlock;
import com.kakaoscan.profile.domain.repository.UserRequestUnlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRequestUnlockService {

    private final UserRequestUnlockRepository userRequestUnlockRepository;

    @Transactional
    public UserRequestUnlock updateUnlockMessage(UserRequestUnlock userRequestUnlock) {
        return userRequestUnlockRepository.save(userRequestUnlock);
    }

    @Transactional
    public UserRequestUnlock findByEmail(String email) {
        return userRequestUnlockRepository.findById(email).orElse(null);
    }
}

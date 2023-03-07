package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.entity.User;
import com.kakaoscan.profile.domain.repository.UserRepository;
import com.kakaoscan.profile.domain.respon.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findByEmail(String email) {
        return userRepository.findById(email).orElse(null);
    }

    @Transactional
    public List<User> findByAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "modifyDt"));
    }

    @Transactional
    public void modifyUser(List<String> emails, Role role, long useCount) {
        for (String email : emails) {
            User user = findByEmail(email);
            user.setRole(role);
            if (user.getRequest() != null) {
                user.getRequest().setUseCount(useCount);
            }
        }
    }
}

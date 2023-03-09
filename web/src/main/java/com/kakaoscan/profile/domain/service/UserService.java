package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.dto.UserModifyDTO;
import com.kakaoscan.profile.domain.entity.User;
import com.kakaoscan.profile.domain.repository.UserRepository;
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
    public void modifyUser(UserModifyDTO userModifyDTO) {
        for (String email : userModifyDTO.getEmails()) {
            User user = findByEmail(email);
            user.setRole(userModifyDTO.getRole());
            if (user.getRequest() != null) {
                user.getRequest().setUseCount(userModifyDTO.getUseCount());
            }
        }
    }
}

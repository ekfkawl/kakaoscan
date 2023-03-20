package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.dto.UserModifyDTO;
import com.kakaoscan.profile.domain.entity.User;
import com.kakaoscan.profile.domain.enums.KafkaEventType;
import com.kakaoscan.profile.domain.enums.Role;
import com.kakaoscan.profile.domain.kafka.service.KafkaProducerService;
import com.kakaoscan.profile.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    private final KafkaProducerService producerService;

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findById(email).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<User> findByAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "modifyDt"));
    }

    @Transactional
    public void modifyUserRole(UserModifyDTO userModifyDTO) {
        for (String email : userModifyDTO.getEmails()) {

            User user = findByEmail(email);
            if (user == null) {
                continue;
            }
            
            // guest -> user 사용 허가 메일 발송
            if (Role.GUEST.getKey().equals(user.getRole().getKey()) && Role.USER.getKey().equals(userModifyDTO.getRole().getKey())) {
                Map<String, Object> map = new HashMap<>();
                map.put("email", user.getEmail());
                producerService.send(KafkaEventType.SEND_MAIL_EVENT, map);
            }
            
            user.setRole(userModifyDTO.getRole());
            if (user.getRequest() != null) {
                user.getRequest().setUseCount(userModifyDTO.getUseCount());
            }
        }
    }

    @Transactional
    public void incTotalUseCount(String email) {
        userRepository.findById(email).ifPresent(User::incTotalUseCount);
    }

}

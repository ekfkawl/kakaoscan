package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.dto.UserModifyDTO;
import com.kakaoscan.profile.domain.entity.User;
import com.kakaoscan.profile.domain.enums.RecordType;
import com.kakaoscan.profile.domain.kafka.service.KafkaProducerService;
import com.kakaoscan.profile.domain.model.KafkaMessage;
import com.kakaoscan.profile.domain.repository.UserRepository;
import com.kakaoscan.profile.domain.enums.Role;
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

    private final KafkaProducerService producerService;

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
            if (user == null) {
                continue;
            }
            
            // guest -> user 사용 허가 메일 발송
            if (Role.GUEST.getKey().equals(user.getRole().getKey()) && Role.USER.getKey().equals(userModifyDTO.getRole().getKey())) {
                producerService.send(user.getEmail(), new KafkaMessage(RecordType.EMAIL, user.getEmail()));
            }
            
            user.setRole(userModifyDTO.getRole());
            if (user.getRequest() != null) {
                user.getRequest().setUseCount(userModifyDTO.getUseCount());
            }
        }
    }
}

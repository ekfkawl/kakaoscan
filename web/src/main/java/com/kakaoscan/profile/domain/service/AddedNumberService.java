package com.kakaoscan.profile.domain.service;

import com.kakaoscan.profile.domain.entity.AddedNumber;
import com.kakaoscan.profile.domain.repository.AddedNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kakaoscan.profile.utils.GenerateUtils.StrToMD5;

@Service
@RequiredArgsConstructor
public class AddedNumberService {

    @Value("${md5.encryption.salt-key}")
    private String saltKey;

    private final AddedNumberRepository addedNumberRepository;

    @Transactional
    public void appendPhoneNumberHash(String phoneNumber) {
        if (isExistsPhoneNumberHash(phoneNumber)) {
            return;
        }
        AddedNumber addedNumber = AddedNumber.builder()
                .phoneNumberHash(StrToMD5(phoneNumber, saltKey))
                .build();
        addedNumberRepository.save(addedNumber);
    }

    @Transactional(readOnly = true)
    public boolean isExistsPhoneNumberHash(String phoneNumber) {
        return addedNumberRepository.existsByPhoneNumberHash(StrToMD5(phoneNumber, saltKey));
    }
}

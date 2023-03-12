package com.kakaoscan.profile.domain.dto;

import com.kakaoscan.profile.domain.entity.UserHistory;
import com.kakaoscan.profile.domain.model.ScanResult;
import lombok.*;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.kakaoscan.profile.utils.DateUtils.getBeforeDiffToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHistoryDTO {
    private String phoneNumber;
    private ScanResult scanResult;
    private LocalDateTime createDt;
    private String remainingPeriod;

    public static UserHistoryDTO toDTO(UserHistory entity) throws IOException {
        return UserHistoryDTO.builder()
                .phoneNumber(entity.getPhoneNumber())
                .scanResult(ScanResult.deserialize(entity.getMessage()))
                .createDt(entity.getCreateDt())
                .remainingPeriod(getBeforeDiffToString(entity.getCreateDt(), LocalDateTime.now()) + " 전")
                .build();
    }
}

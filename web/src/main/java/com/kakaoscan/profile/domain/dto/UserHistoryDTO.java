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
    private LocalDateTime modifyDt;
    private String remainingPeriod;

    public static UserHistoryDTO toDTO(UserHistory entity) throws IOException {
        return UserHistoryDTO.builder()
                .phoneNumber(entity.getPhoneNumber())
                .scanResult(ScanResult.deserialize(entity.getMessage()))
                .modifyDt(entity.getModifyDt())
                .remainingPeriod(getBeforeDiffToString(entity.getModifyDt(), LocalDateTime.now()) + " 전")
                .build();
    }
}

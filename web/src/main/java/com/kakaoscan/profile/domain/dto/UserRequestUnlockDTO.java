package com.kakaoscan.profile.domain.dto;

import com.kakaoscan.profile.domain.entity.UserRequestUnlock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestUnlockDTO {

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max=1000, message = "1000자 이하로 입력해주세요.")
    private String message;

    public UserRequestUnlock toEntity(String email) {
        return UserRequestUnlock.builder()
                .email(email)
                .message(message)
                .modifyDt(LocalDateTime.now())
                .build();
    }
}

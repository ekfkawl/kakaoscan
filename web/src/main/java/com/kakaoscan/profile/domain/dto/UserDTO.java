package com.kakaoscan.profile.domain.dto;

import com.kakaoscan.profile.domain.entity.User;
import com.kakaoscan.profile.domain.entity.UserRequestUnlock;
import com.kakaoscan.profile.domain.respon.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String email;
    private Role role;
    private long useCount;
    private LocalDateTime modifyDt;
    private UserRequestUnlock requestUnlock;

    public static UserDTO toDTO(User entity) {
        return UserDTO.builder()
                .email(entity.getEmail())
                .role(entity.getRole())
                .modifyDt(entity.getModifyDt())
                .requestUnlock(entity.getRequestUnlock())
                .useCount(entity.getRequest() != null ? entity.getRequest().getUseCount() : 0)
                .build();
    }
}

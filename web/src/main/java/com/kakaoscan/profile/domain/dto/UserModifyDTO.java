package com.kakaoscan.profile.domain.dto;

import com.kakaoscan.profile.domain.respon.enums.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyDTO {
    private List<String> emails;
    private Role role;
    private long useCount;
}

package com.kakaoscan.server.application.dto.response;

import com.kakaoscan.server.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
public class UserData {
    private String email;
    private Role role;
    @Nullable
    private String profileUrl;
}

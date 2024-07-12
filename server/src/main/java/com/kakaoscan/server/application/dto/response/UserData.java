package com.kakaoscan.server.application.dto.response;

import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@AllArgsConstructor
public class UserData {
    private String email;
    private Role role;
    private List<UserItem> items;
    @Nullable
    private String profileUrl;
    private AuthenticationType authenticationType;
}

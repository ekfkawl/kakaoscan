package com.kakaoscan.server.application.dto;

import com.kakaoscan.server.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserData {
    private String email;
    private Role role;
}

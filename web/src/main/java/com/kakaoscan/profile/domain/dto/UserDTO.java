package com.kakaoscan.profile.domain.dto;

import com.kakaoscan.profile.domain.entity.User;
import com.kakaoscan.profile.domain.entity.UserRequestUnlock;
import com.kakaoscan.profile.domain.enums.Role;
import com.kakaoscan.profile.global.oauth.OAuthAttributes;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    @Id
    private String email;
    private String name;
    private String profileUrl;
    private Role role;
    private transient long useCount;
    private transient long totalUseCount;
    private transient LocalDateTime modifyDt;
    private transient LocalDateTime createDt;
    private transient UserRequestUnlock requestUnlock;

    public static UserDTO toDTO(User entity) {
        return UserDTO.builder()
                .email(entity.getEmail())
                .role(entity.getRole())
                .modifyDt(entity.getModifyDt())
                .createDt(entity.getCreateDt())
                .requestUnlock(entity.getRequestUnlock())
                .useCount(entity.getRequest() != null ? entity.getRequest().getUseCount() : -1)
                .totalUseCount(entity.getTotalUseCount())
                .build();
    }

    public static UserDTO toDTO(OAuthAttributes attributes) {
        return UserDTO.builder()
                .email(attributes.getEmail())
                .name(attributes.getName())
                .profileUrl(attributes.getProfileUrl())
                .role(attributes.getRole())
                .build();
    }
}

package com.kakaoscan.profile.domain.entity;

import com.kakaoscan.profile.domain.respon.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "tb_user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class User {
    /**
     * 이메일 아이디
     */
    @Id
    private String email;
    /**
     * 권한
     */
    @Enumerated(EnumType.STRING)
    private Role role;
    /**
     * 수정 날짜
     */
    @UpdateTimestamp
    private LocalDateTime modifyDt;
}

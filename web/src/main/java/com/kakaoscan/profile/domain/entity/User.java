package com.kakaoscan.profile.domain.entity;

import com.kakaoscan.profile.domain.respon.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @CreationTimestamp
    private LocalDateTime createDt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserRequestUnlock requestUnlock;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserRequest request;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserHistory> historyList;

    public void setRole(Role role) {
        this.role = role;
    }
}

package com.kakaoscan.profile.domain.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "tb_user_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private String email;

    private String phoneNumber;

    private String message;

    @UpdateTimestamp
    private LocalDateTime modifyDt;

    @CreationTimestamp
    private LocalDateTime createDt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", insertable = false, updatable = false)
    private User user;

    public void update(String email, String phoneNumber, String message) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.message = message;
    }
}

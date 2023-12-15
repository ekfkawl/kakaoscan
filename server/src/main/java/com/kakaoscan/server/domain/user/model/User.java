package com.kakaoscan.server.domain.user.model;

import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 8)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isEmailVerified;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    @UpdateTimestamp
    @Column(columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    protected User() {
    }

    public void verifyEmail() {
        this.isEmailVerified = true;
    }
}

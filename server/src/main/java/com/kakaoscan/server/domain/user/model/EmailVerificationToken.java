package com.kakaoscan.server.domain.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "email_verification_tokens", indexes = {
        @Index(name = "idx_email_verification_token", columnList = "token", unique = true)
})
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    protected EmailVerificationToken() {
    }

    public EmailVerificationToken(User user, String token) {
        this.user = user;
        this.token = token;
    }
}

package com.kakaoscan.server.domain.search.entity;

import com.kakaoscan.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "new_phone_number", indexes = {
        @Index(name = "idx_target_phone_number", columnList = "targetPhoneNumber"),
        @Index(name = "idx_created_at", columnList = "createdAt")
})
public class NewPhoneNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String targetPhoneNumber;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    protected NewPhoneNumber() {
    }
}

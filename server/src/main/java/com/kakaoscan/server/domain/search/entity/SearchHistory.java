package com.kakaoscan.server.domain.search.entity;

import com.kakaoscan.server.domain.search.enums.CostType;
import com.kakaoscan.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "search_history", indexes = {
        @Index(name = "idx_phone_cost_created", columnList = "targetPhoneNumber,costType,createdAt DESC"),
        @Index(name = "idx_user_created", columnList = "user_id,createdAt DESC")
})
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String targetPhoneNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String data; // ALTER TABLE search_history CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

//    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CostType costType;

    protected SearchHistory() {
    }
}

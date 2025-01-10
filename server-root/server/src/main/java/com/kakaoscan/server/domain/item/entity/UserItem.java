package com.kakaoscan.server.domain.item.entity;

import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "user_item", indexes = {
        @Index(name = "idx_product_type", columnList = "product_type"),
        @Index(name = "idx_expiredAt", columnList = "expired_at DESC")
})
public class UserItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime expiredAt;

    public void renew() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(expiredAt)) {
            expiredAt = now.plusDays(30);
        }else {
            expiredAt = expiredAt.plusDays(30);
        }
    }
}

package com.kakaoscan.server.domain.point.entity;

import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.product.enums.ProductType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "points_transaction", indexes = {
        @Index(name = "idx_transaction_status", columnList = "transactionStatus"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_status_created_at", columnList = "transactionStatus, createdAt")
})
public class PointTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductTransactionStatus transactionStatus;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column(nullable = false)
    private String depositor;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

    @Setter
    @ManyToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private PointWallet wallet;

    protected PointTransaction() {
    }

    public void cancelTransaction() {
        this.transactionStatus = ProductTransactionStatus.CANCELLED;
    }

    public void approvalTransaction() {
        this.transactionStatus = ProductTransactionStatus.EARNED;
    }
}

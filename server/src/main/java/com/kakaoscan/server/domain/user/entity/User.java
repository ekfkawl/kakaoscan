package com.kakaoscan.server.domain.user.entity;

import com.kakaoscan.server.domain.item.entity.UserItem;
import com.kakaoscan.server.domain.point.entity.PointWallet;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.product.model.PaymentRequest;
import com.kakaoscan.server.domain.search.entity.NewPhoneNumber;
import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.*;

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

//    @NotBlank
//    @Size(min = 8)
//    @Column(nullable = false)
    @Setter
    private String password;

    @Column(nullable = false)
    @Setter
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

    @Column(nullable = false)
    @Setter
    private boolean isDeleted = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private PointWallet pointWallet;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<SearchHistory> searchHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<NewPhoneNumber> newPhoneNumbers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("expiredAt DESC")
    private List<UserItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<ProductTransaction> productTransactions = new ArrayList<>();

    protected User() {
    }

    public void verifyEmail() {
        this.isEmailVerified = true;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.getRole().name()));

        return authorities;
    }

    public void initializePoint() {
        this.pointWallet = PointWallet.builder()
                .user(this)
                .balance(0)
                .build();
    }

    public void addSearchHistory(SearchHistory searchHistory) {
        searchHistory.setUser(this);
        this.searchHistories.add(searchHistory);
    }

    public void addNewPhoneNumbers(NewPhoneNumber newPhoneNumber) {
        newPhoneNumber.setUser(this);
        this.newPhoneNumbers.add(newPhoneNumber);
    }

    public void addUserItem(UserItem userItem) {
        userItem.setUser(this);
        this.items.add(userItem);
    }

    public ProductTransaction addPendingTransaction(PaymentRequest paymentRequest, String depositor) {
        ProductTransaction transaction = ProductTransaction.builder()
                .amount(paymentRequest.getAmount())
                .productType(paymentRequest.getProductType())
                .transactionStatus(ProductTransactionStatus.PENDING)
                .depositor(depositor)
                .user(this)
                .wallet(this.pointWallet)
                .build();

        this.productTransactions.add(transaction);

        return transaction;
    }
}

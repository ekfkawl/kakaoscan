package com.kakaoscan.server.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductTransactions {
    private final List<ProductTransactionResponse> productTransactionList = new ArrayList<>();
    private long totalCount;
    private String account;

    public ProductTransactions(String backAccount) {
        this.account = backAccount;
    }

    public void addTransaction(ProductTransactionResponse transaction) {
        this.productTransactionList.add(transaction);
    }

    public static ProductTransactions convertToProductTransactions(List<ProductTransaction> transactions, long totalCount, String account) {
        ProductTransactions productTransactions = new ProductTransactions(account);

        transactions.forEach(transaction ->
                productTransactions.addTransaction(new ProductTransactionResponse(
                        transaction.getId(),
                        transaction.getWallet().getUser().getEmail(),
                        transaction.getTransactionStatus().getDisplayName(),
                        transaction.getProductType().getDisplayName(),
                        transaction.getAmount(),
                        transaction.getDepositor(),
                        transaction.getCreatedAt(),
                        transaction.getUpdatedAt()
                )));

        productTransactions.totalCount = totalCount;

        return productTransactions;
    }

    @Getter
    @AllArgsConstructor
    public static class ProductTransactionResponse {
        private long id;
        private String email;
        private String productTransactionStatus;
        private String productName;
        private int amount;
        private String depositor;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}

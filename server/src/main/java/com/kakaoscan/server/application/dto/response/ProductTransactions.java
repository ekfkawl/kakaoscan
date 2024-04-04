package com.kakaoscan.server.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductTransactions {
    private final List<ProductTransactions.ProductTransactionResponse> productTransactionList;
    private final String account;

    public ProductTransactions(String backAccount) {
        this.productTransactionList = new ArrayList<>();
        this.account = backAccount;
    }

    public void addTransaction(ProductTransactionResponse transaction) {
        this.productTransactionList.add(transaction);
    }

    @Getter
    @AllArgsConstructor
    public static class ProductTransactionResponse {
        private long id;
        private String productTransactionStatus;
        private String productName;
        private int amount;
        private String depositor;
        private LocalDateTime createdAt;
    }
}

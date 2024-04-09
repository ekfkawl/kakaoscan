package com.kakaoscan.server.application.service;

import com.kakaoscan.server.application.dto.response.ProductTransactions;
import com.kakaoscan.server.domain.point.repository.PointWalletRepository;
import com.kakaoscan.server.domain.product.entity.ProductTransaction;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.domain.product.repository.ProductTransactionRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductService {
    private final UserRepository userRepository;
    private final ProductTransactionRepository productTransactionRepository;
    private final PointWalletRepository pointWalletRepository;

    @Value("${bank.account}")
    private String backAccount;

    @Transactional(readOnly = true)
    public ProductTransactions findProductTransactionsByPointWallet(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userRepository.findByEmailOrThrow(userId);
        List<ProductTransaction> productTransactionList = pointWalletRepository.findProductTransactionsByPointWallet(user.getPointWallet(), startDate, endDate);

        return ProductTransactions.convertToProductTransactions(productTransactionList, productTransactionList.size(), backAccount);
    }

    @Transactional(readOnly = true)
    public ProductTransactions findAndFilterTransactions(LocalDateTime startDate, LocalDateTime endDate, ProductTransactionStatus status, String keyword, int page, int pageSize) {
        QueryResults<ProductTransaction> transactionQueryResults = productTransactionRepository.findAndFilterTransactions(startDate, endDate, status, keyword, page, pageSize);

        List<ProductTransaction> filteredTransactions = transactionQueryResults.getResults();

        return ProductTransactions.convertToProductTransactions(filteredTransactions, transactionQueryResults.getTotal(), null);
    }
}
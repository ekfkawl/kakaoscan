package com.kakaoscan.server.application.controller.api.admin;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.dto.response.ProductTransactions;
import com.kakaoscan.server.application.service.ProductService;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.infrastructure.security.validation.AdminRole;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RequiredArgsConstructor
@RestController
@Tag(name = "Admin", description = "Admin API")
public class AdminController extends ApiEndpointPrefix {
    private final ProductService productService;

    @AdminRole
    @GetMapping("/admin/product/transactions")
    public ResponseEntity<ApiResponse<ProductTransactions>> findAndFilterTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) ProductTransactionStatus status,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        ProductTransactions transactions = productService.findAndFilterTransactions(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), status, keyword, page, pageSize);

        return new ResponseEntity<>(ApiResponse.success(transactions), HttpStatus.OK);
    }
}

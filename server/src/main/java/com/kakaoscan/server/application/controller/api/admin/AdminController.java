package com.kakaoscan.server.application.controller.api.admin;

import com.kakaoscan.server.application.controller.ApiEndpointPrefix;
import com.kakaoscan.server.application.dto.response.ApiResponse;
import com.kakaoscan.server.application.dto.response.AppLogs;
import com.kakaoscan.server.application.dto.response.ProductTransactions;
import com.kakaoscan.server.application.service.ProductService;
import com.kakaoscan.server.domain.product.enums.ProductTransactionStatus;
import com.kakaoscan.server.infrastructure.logging.enums.LogLevel;
import com.kakaoscan.server.infrastructure.security.validation.AdminRole;
import com.kakaoscan.server.infrastructure.service.LoggingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Tag(name = "Admin", description = "Admin API")
public class AdminController extends ApiEndpointPrefix {
    private final ProductService productService;
    private final LoggingService loggingService;

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

    @AdminRole
    @PutMapping("/admin/product/approval")
    public ResponseEntity<ApiResponse<Void>> approvalTransaction(@RequestBody Map<String, Long> payload) {
        productService.approvalTransaction(payload.get("transactionId"));

        return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
    }

    @AdminRole
    @PutMapping("/admin/product/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelTransaction(@RequestBody Map<String, Long> payload) {
        productService.cancelTransaction(payload.get("transactionId"));

        return new ResponseEntity<>(ApiResponse.success(), HttpStatus.OK);
    }

    @AdminRole
    @GetMapping("/admin/log")
    public ResponseEntity<ApiResponse<AppLogs>> findAndFilterLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) LogLevel level,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        AppLogs logs = loggingService.findAndFilterLogs(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), level, keyword, page, pageSize);

        return new ResponseEntity<>(ApiResponse.success(logs), HttpStatus.OK);
    }
}

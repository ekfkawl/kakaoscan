package com.kakaoscan.server.domain.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductTransactionStatus {
    PENDING("대기"),
    CANCELLED("취소"),
    EARNED("완료");

    private final String displayName;
}

package com.kakaoscan.server.domain.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {
    P500("500 P"),
    P1000("1,000 P"),
    P5000("5,000 P"),
    UNKNOWN("Unknown");

    private final String displayName;
}

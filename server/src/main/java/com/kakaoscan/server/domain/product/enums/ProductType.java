package com.kakaoscan.server.domain.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {
    P500("500 P"),
    P1000("1,000 P"),
    P1500("1,500 P"),
    P2000("2,000 P"),
    P3000("3,000 P"),
    P5000("5,000 P"),
    P10000("10,000 P"),
    SNAPSHOT_PRESERVATION("스냅샷 보존권 (30일)"),
    UNKNOWN("Unknown");

    private final String displayName;
}

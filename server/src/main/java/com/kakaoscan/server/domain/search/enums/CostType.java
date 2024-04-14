package com.kakaoscan.server.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CostType {
    ORIGIN(500),
    DISCOUNT(250),
    FREE(0);

    private final int cost;
}

package com.kakaoscan.server.application.dto.response;

import com.kakaoscan.server.domain.product.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserItem {
    private ProductType productType;
    private String productName;
    private LocalDateTime expiredAt;
}

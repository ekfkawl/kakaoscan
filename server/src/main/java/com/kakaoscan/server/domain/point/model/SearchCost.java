package com.kakaoscan.server.domain.point.model;

import com.kakaoscan.server.application.dto.response.TargetSearchCost;
import com.kakaoscan.server.domain.search.enums.CostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCost implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private CostType costType;
    private int cost;
    private LocalDateTime expiredAtDiscount;

    public TargetSearchCost convertToTargetSearchCost() {
        return new TargetSearchCost(this.costType, this.cost, this.expiredAtDiscount);
    }
}

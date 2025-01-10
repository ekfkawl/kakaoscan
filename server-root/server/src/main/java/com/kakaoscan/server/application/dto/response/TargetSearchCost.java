package com.kakaoscan.server.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.search.enums.CostType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TargetSearchCost extends SearchCost {

    public TargetSearchCost(CostType costType, int cost, LocalDateTime expiredAtDiscount) {
        super(costType, cost, expiredAtDiscount);
    }
}

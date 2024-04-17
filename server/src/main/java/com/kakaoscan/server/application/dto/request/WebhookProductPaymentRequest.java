package com.kakaoscan.server.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookProductPaymentRequest {
    @JsonProperty("mall_id")
    private String mallId;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty("processing_date")
    private OffsetDateTime processingDate;
}
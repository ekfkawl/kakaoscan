package com.kakaoscan.server.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class WebhookProductOrderRequest {
    @JsonProperty("apikey")
    private final String apiKey;

    @JsonProperty("secretkey")
    private final String secretKey;

    @JsonProperty("mall_id")
    private final String mallId;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("order_amount")
    private int orderAmount;

    @JsonProperty("order_date")
    private String orderDate;

    @JsonProperty("orderer_name")
    private String ordererName;

    @JsonProperty("orderer_phone_number")
    private String ordererPhoneNumber;

    @JsonProperty("orderer_email")
    private String ordererEmail;

    @JsonProperty("billing_name")
    private String billingName;

    @Builder
    public WebhookProductOrderRequest(String orderNumber, int orderAmount, String ordererName, String ordererEmail, String billingName) {
        this.apiKey = System.getenv("PAY_API_KEY");
        this.secretKey = System.getenv("PAY_SECRET_KEY");
        this.mallId = System.getenv("PAY_MALL_ID");
        this.orderNumber = orderNumber;
        this.orderAmount = orderAmount;
        this.orderDate = OffsetDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.ordererName = ordererName;
        this.ordererPhoneNumber = "Empty";
        this.ordererEmail = ordererEmail;
        this.billingName = billingName;
    }
}
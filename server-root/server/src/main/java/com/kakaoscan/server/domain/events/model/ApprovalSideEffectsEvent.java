package com.kakaoscan.server.domain.events.model;

public record ApprovalSideEffectsEvent(
    String email,
    int newBalance,
    String productName,
    String baseUrl,
    Long transactionId
) {}
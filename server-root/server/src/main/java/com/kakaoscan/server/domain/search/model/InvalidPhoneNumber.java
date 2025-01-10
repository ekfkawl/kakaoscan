package com.kakaoscan.server.domain.search.model;

import java.time.LocalDateTime;

public record InvalidPhoneNumber(String email, LocalDateTime createdAt) {
}

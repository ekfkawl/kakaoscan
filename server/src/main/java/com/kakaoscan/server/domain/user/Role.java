package com.kakaoscan.server.domain.user;

public enum Role {
    USER,
    ADMIN;

    public static Role fromAuthority(String authority) {
        return switch (authority) {
            case "ROLE_USER" -> USER;
            case "ROLE_ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException("unknown authority: " + authority);
        };
    }
}

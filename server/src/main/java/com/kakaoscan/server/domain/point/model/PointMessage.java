package com.kakaoscan.server.domain.point.model;

import com.kakaoscan.server.domain.websocket.model.MessageMetadata;
import lombok.Getter;

@Getter
public class PointMessage extends MessageMetadata {
    private final int balance;
    private final String message;

    public PointMessage(String email, int balance) {
        super(email);
        this.balance = balance;
        this.message = null;
    }

    public PointMessage(String email, int balance, String message) {
        super(email);
        this.balance = balance;
        this.message = message;
    }
}

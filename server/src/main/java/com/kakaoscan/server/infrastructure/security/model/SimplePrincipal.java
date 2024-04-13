package com.kakaoscan.server.infrastructure.security.model;

import lombok.AllArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
public class SimplePrincipal implements Principal {
    private String name;

    @Override
    public String getName() {
        return this.name;
    }
}
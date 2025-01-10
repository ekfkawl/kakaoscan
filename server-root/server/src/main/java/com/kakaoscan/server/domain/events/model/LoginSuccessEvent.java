package com.kakaoscan.server.domain.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessEvent extends EventMetadata {
    private String email;
}

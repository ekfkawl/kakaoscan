package com.kakaoscan.server.domain.events.model;

import com.kakaoscan.server.infrastructure.email.types.VerificationEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationEmailEvent extends EventMetadata {
    private VerificationEmail verificationEmail;
}

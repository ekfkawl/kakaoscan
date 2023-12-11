package com.kakaoscan.server.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate {
    private String receiver;
    private String subject;
    private String html;
}

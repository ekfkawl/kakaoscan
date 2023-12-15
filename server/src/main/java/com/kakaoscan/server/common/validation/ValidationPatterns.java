package com.kakaoscan.server.common.validation;

public class ValidationPatterns {
    public static final String EMAIL_AND_DOMAIN = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@(naver\\.com|daum\\.net)$";
    public static final String PASSWORD = "^[A-Za-z\\d~!@#$%^&*()_+]{8,16}$";
}

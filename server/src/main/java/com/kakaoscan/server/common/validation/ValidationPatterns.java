package com.kakaoscan.server.common.validation;

public class ValidationPatterns {
    public static final String EMAIL_AND_DOMAIN = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@(naver\\.com|daum\\.net)$";
    public static final String PASSWORD = "^[A-Za-z\\d~!@#$%^&*()_+]{8,16}$";
    public static final String ONLY_NUMBER = "\\d+";
    public static final String PHONE_NUMBER = "010\\d{8}";
    public static final String KAKAO_ID = "^[a-zA-Z0-9]{2,20}$" ;
}

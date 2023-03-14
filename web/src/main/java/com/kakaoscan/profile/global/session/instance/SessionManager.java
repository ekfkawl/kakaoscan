package com.kakaoscan.profile.global.session.instance;

public interface SessionManager {
    public static final String SESSION_FORMAT = "user:%s";
    public static final String SESSION_KEY = "email-hash";
    void setValue(String key, Object value);
    Object getValue(String key);
    void deleteValue(String key);
}

package com.kakaoscan.profile.global.session.instance;

public interface SessionManager {
    void setValue(String key, Object value);
    Object getValue(String key);
    void deleteValue(String key);
}

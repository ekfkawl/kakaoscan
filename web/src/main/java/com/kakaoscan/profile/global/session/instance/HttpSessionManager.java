package com.kakaoscan.profile.global.session.instance;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
@Profile("!prod")
public class HttpSessionManager implements SessionManager {
    private final HttpSession httpSession;

    public HttpSessionManager(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public void setValue(String key, Object value) {
        httpSession.setAttribute(key, value);
    }

    @Override
    public Object getValue(String key) {
        return httpSession.getAttribute(key);
    }

    @Override
    public void deleteValue(String key) {
        httpSession.removeAttribute(key);
    }
}

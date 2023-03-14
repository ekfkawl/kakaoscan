package com.kakaoscan.profile.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

import static com.kakaoscan.profile.global.session.instance.SessionManager.SESSION_KEY;

public class HttpRequestUtils {
    public static String getRemoteAddress(HttpServletRequest request) {
        String remoteAddress = (null != request.getHeader("X-FORWARDED-FOR")) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
        String ra = remoteAddress.replace("0:0:0:0:0:0:0:1", "127.0.0.1");
        if (ra.contains(",")) {
            ra = ra.split(",")[0];
        }
        return ra;
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.of(null);
        }
        return Arrays.stream(cookies)
                .filter(cookie -> SESSION_KEY.equals(cookie.getName()))
                .findFirst();
    }
}

package com.kakaoscan.profile.utils;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestUtils {
    public static String getRemoteAddress(HttpServletRequest request){
        String remoteAddress = (null != request.getHeader("X-FORWARDED-FOR")) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
        String ra = remoteAddress.replace("0:0:0:0:0:0:0:1", "127.0.0.1");
        if (ra.contains(",")) {
            ra = ra.split(",")[0];
        }
        return ra;
    }
}

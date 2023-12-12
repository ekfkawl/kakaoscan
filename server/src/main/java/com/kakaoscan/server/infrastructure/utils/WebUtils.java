package com.kakaoscan.server.infrastructure.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WebUtils {
    public static String getRemoteAddress(HttpServletRequest request) {
        String remoteAddress = (null != request.getHeader("X-FORWARDED-FOR")) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
        String ra = remoteAddress.replace("0:0:0:0:0:0:0:1", "127.0.0.1");
        if (ra.contains(",")) {
            ra = ra.split(",")[0];
        }
        try {
            InetAddress inetAddress = InetAddress.getByName(ra);
            if (inetAddress instanceof Inet4Address) {
                return ra;
            } else {
                byte[] bytes = inetAddress.getAddress();
                InetAddress v4Address = Inet4Address.getByAddress(bytes);
                return v4Address.getHostAddress();
            }
        } catch (UnknownHostException e) {
            return ra;
        }
    }
}

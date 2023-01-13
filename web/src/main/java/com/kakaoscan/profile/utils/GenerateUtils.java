package com.kakaoscan.profile.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GenerateUtils {
    /**
     * to md5
     * @param s
     * @param salt
     * @return
     */
    public static String StrToMD5(String s, String salt) {
        s += salt;
        String MD5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }
}

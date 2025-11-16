package com.heima.utils.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class Base64Utils {


    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }


    public static String encodeBuffer(byte[] bytes) {
        return Base64.getMimeEncoder().encodeToString(bytes);
    }


    public static byte[] decode(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }


    public static String encode(String text) {
        return encode(text.getBytes(StandardCharsets.UTF_8));
    }


    public static String decodeToString(String base64Str) {
        byte[] decodedBytes = decode(base64Str);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }


}
package com.ruoyi.uni.util;

public class ValidateUtil {
    /**
     * 手机号正则表达式
     */
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 邮箱正则表达式
     */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    /**
     * 验证是否为手机号
     */
    public static boolean isPhone(String input) {
        return input != null && input.matches(PHONE_REGEX);
    }

    /**
     * 验证是否为邮箱
     */
    public static boolean isEmail(String input) {
        return input != null && input.matches(EMAIL_REGEX);
    }
}
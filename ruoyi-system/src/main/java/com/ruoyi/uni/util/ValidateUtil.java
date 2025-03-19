package com.ruoyi.uni.util;

import com.ruoyi.common.utils.StringUtils;

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

    public static boolean isIdCard(String idCard) {
        if (StringUtils.isEmpty(idCard)) {
            return false;
        }

        // 18位身份证正则表达式
        String regex18 = "^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$";
        // 15位身份证正则表达式
        String regex15 = "^[1-9]\\d{5}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$";

        return idCard.matches(regex18) || idCard.matches(regex15);
    }
}
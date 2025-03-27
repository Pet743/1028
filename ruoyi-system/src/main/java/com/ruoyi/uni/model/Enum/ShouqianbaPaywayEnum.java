package com.ruoyi.uni.model.Enum;

import com.ruoyi.common.utils.StringUtils;
import lombok.Getter;

/**
 * 收钱吧支付方式枚举
 */
@Getter
public enum ShouqianbaPaywayEnum {

    ZFB("zfb", "1", "支付宝"),
    VX("vx", "3", "微信支付"),
    YL("yl", "18", "翼支付");

    /**
     * 前端传入的代码
     */
    private final String code;

    /**
     * 收钱吧支付方式值
     */
    private final String value;

    /**
     * 支付方式名称
     */
    private final String name;

    ShouqianbaPaywayEnum(String code, String value, String name) {
        this.code = code;
        this.value = value;
        this.name = name;
    }

    /**
     * 根据前端代码获取支付方式枚举
     */
    public static ShouqianbaPaywayEnum getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return ZFB; // 默认支付宝
        }

        for (ShouqianbaPaywayEnum payway : values()) {
            if (payway.getCode().equalsIgnoreCase(code)) {
                return payway;
            }
        }
        return ZFB; // 找不到默认支付宝
    }

    /**
     * 获取收钱吧支付方式值
     */
    public static String getValueByCode(String code) {
        return getByCode(code).getValue();
    }
}
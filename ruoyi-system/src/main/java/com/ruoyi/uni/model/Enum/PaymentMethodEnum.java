package com.ruoyi.uni.model.Enum;

import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
public enum PaymentMethodEnum {

    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信"),
    BANK_CARD(3, "银行卡"),
    WALLET(4, "钱包余额");

    private final int code;
    private final String desc;

    PaymentMethodEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PaymentMethodEnum getByCode(int code) {
        for (PaymentMethodEnum method : values()) {
            if (method.getCode() == code) {
                return method;
            }
        }
        return null;
    }
}
package com.ruoyi.uni.model.Enum;

import lombok.Getter;
import java.util.Arrays;

/**
 * 支付方式枚举
 */
@Getter
public enum PayChannelEnum {
    ALIPAY("zfb", "1", 1, "支付宝"),
    WECHAT("vx", "3", 2, "微信支付"),
    BANK_CARD("yinghang","",3, "银行卡"),
    WALLET("qianbao","",4, "钱包余额"),
    SHOUQIANBA("zfb", "18", 5, "收钱吧"),
    HUIFU_MINIAPP("zfb","", 6, "汇付支付宝"),
    HUIFU_ALIPAY("vx","", 7, "汇付微信");


    /**
     * 前端传入的代码
     */
    private final String code;

    /**
     * 三方支付方式值
     */
    private final String value;

    /**
     * 系统支付方式值
     */
    private final int system;

    /**
     * 支付方式名称
     */
    private final String name;

    PayChannelEnum(String code, String value, Integer system, String name) {
        this.code = code;
        this.value = value;
        this.system = system;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public Integer getSystem() {
        return system;
    }

    public String getName() {
        return name;
    }

    public static PayChannelEnum getByCode(String code) {
        return Arrays.stream(values())
                .filter(channel -> channel.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}

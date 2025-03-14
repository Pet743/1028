package com.ruoyi.uni.model.Enum;


/**
 * 发货方式枚举
 */
public enum ShippingMethodEnum {

    EXPRESS("express", "快递"),
    EMS("ems", "EMS"),
    SELF_PICKUP("self_pickup", "自提"),
    FREE_SHIPPING("free_shipping", "包邮");

    private final String code;
    private final String desc;

    ShippingMethodEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取枚举
     */
    public static ShippingMethodEnum getByCode(String code) {
        if (code == null) {
            return EXPRESS;
        }

        for (ShippingMethodEnum method : values()) {
            if (method.getCode().equals(code)) {
                return method;
            }
        }
        return EXPRESS;
    }
}
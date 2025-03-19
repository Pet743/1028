package com.ruoyi.uni.model.Enum;


/**
 * 发货方式枚举
 */
public enum ShippingMethodEnum {

    EXPRESS("express", "快递发货"),
    SELF_PICKUP("self_pickup", "同城自提"),
    FACE_TO_FACE("face_to_face", "当面交易");

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
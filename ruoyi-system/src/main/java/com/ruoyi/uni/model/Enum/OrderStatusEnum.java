package com.ruoyi.uni.model.Enum;

import lombok.Getter;


/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatusEnum {

    ALL(0, "全部"),
    PENDING_PAYMENT(1, "待付款"),
    PENDING_SHIPMENT(2, "待发货"),
    PENDING_RECEIPT(3, "待收货"),
    PENDING_REVIEW(4, "待评价"),
    REFUNDING(5, "退款中"),
    CLOSED(6, "已关闭");

    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatusEnum getByCode(int code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static boolean isValid(int code) {
        return getByCode(code) != null;
    }
}
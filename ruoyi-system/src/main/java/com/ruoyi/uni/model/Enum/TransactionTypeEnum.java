package com.ruoyi.uni.model.Enum;

import lombok.Getter;

/**
 * 交易类型枚举
 */
@Getter
public enum TransactionTypeEnum {

    BUY_PRODUCT(1, "购买商品"),
    SELL_PRODUCT(2, "售出商品"),
    RECHARGE(3, "钱包充值"),
    WITHDRAW(4, "提现");

    private final int code;
    private final String desc;

    TransactionTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取枚举
     */
    public static TransactionTypeEnum getByCode(Integer code) {
        if (code == null) {
            return BUY_PRODUCT;
        }

        for (TransactionTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return BUY_PRODUCT;
    }
}
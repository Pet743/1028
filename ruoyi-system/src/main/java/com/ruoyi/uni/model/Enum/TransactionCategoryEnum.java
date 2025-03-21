package com.ruoyi.uni.model.Enum;

import lombok.Getter;

/**
 * 交易类别枚举
 */
@Getter
public enum TransactionCategoryEnum {

    ALL(0, "全部"),
    INCOME(1, "收入"),
    EXPENSE(2, "支出");

    private final int code;
    private final String desc;

    TransactionCategoryEnum(int code, String desc) {
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
    public static TransactionCategoryEnum getByCode(Integer code) {
        if (code == null) {
            return ALL;
        }

        for (TransactionCategoryEnum category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return ALL;
    }
}
package com.ruoyi.uni.model.Enum;

/**
 * 商品类别枚举
 */
public enum ProductCategoryEnum {

    ELECTRONICS("electronics", "电子产品"),
    CLOTHING("clothing", "服装"),
    FOOD("food", "食品"),
    BOOKS("books", "图书"),
    HOME("home", "家居"),
    BEAUTY("beauty", "美妆"),
    SPORTS("sports", "运动"),
    TOYS("toys", "玩具"),
    OTHER("other", "其他");

    private final String code;
    private final String desc;

    ProductCategoryEnum(String code, String desc) {
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
    public static ProductCategoryEnum getByCode(String code) {
        if (code == null) {
            return OTHER;
        }

        for (ProductCategoryEnum category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return OTHER;
    }
}
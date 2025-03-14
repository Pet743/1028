package com.ruoyi.uni.model.Enum;

import com.ruoyi.alse.domain.AlseProduct;

import java.util.Date;
import java.util.function.Consumer;

/**
 * 商品操作类型枚举
 */
public enum ProductOperationTypeEnum {

    SHELF("0", "上架", product -> {
        product.setProductStatus("0");
        product.setUpdateTime(new Date());
    }),

    UNSHELF("1", "下架", product -> {
        product.setProductStatus("1");
        product.setUpdateTime(new Date());
    }),

    DELETE("2", "删除", product -> {
        product.setStatus("1"); // 软删除，修改状态为停用
        product.setUpdateTime(new Date());
    });

    private final String code;
    private final String desc;
    private final Consumer<AlseProduct> operation;

    ProductOperationTypeEnum(String code, String desc, Consumer<AlseProduct> operation) {
        this.code = code;
        this.desc = desc;
        this.operation = operation;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Consumer<AlseProduct> getOperation() {
        return operation;
    }

    /**
     * 根据code获取枚举
     */
    public static ProductOperationTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }

        for (ProductOperationTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
package com.ruoyi.uni.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 佣金阶梯定义
 */
@Data
@AllArgsConstructor
public
class CommissionTier {
    /** 金额上限 */
    private BigDecimal upperLimit;
    /** 费率(百分比) */
    private BigDecimal rate;
}
package com.ruoyi.uni.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易流水统计结果
 */
@Data
@AllArgsConstructor
public
class TransactionStats {
    /** 收入金额 */
    private BigDecimal income;
    /** 支出金额 */
    private BigDecimal expense;
    /** 余额 */
    private BigDecimal balance;
    /** 交易笔数 */
    private Long count;
}
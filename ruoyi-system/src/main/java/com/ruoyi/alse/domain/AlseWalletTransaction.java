package com.ruoyi.alse.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 钱包交易流水对象 alse_wallet_transaction
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
public class AlseWalletTransaction extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 交易ID */
    private Long transactionId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String username;

    /** 交易金额 */
    @Excel(name = "交易金额")
    private BigDecimal transactionAmount;

    /** 收入金额 */
    @Excel(name = "收入金额")
    private BigDecimal incomeAmount;

    /** 支出金额 */
    @Excel(name = "支出金额")
    private BigDecimal expenseAmount;

    /** 支付方式(1:支付宝 2:微信 3:银行卡) */
    @Excel(name = "支付方式(1:支付宝 2:微信 3:银行卡)")
    private Integer paymentMethod;

    /** 交易类型(1:购买商品 2:售出商品 3:钱包充值 4:提现) */
    @Excel(name = "交易类型(1:购买商品 2:售出商品 3:钱包充值 4:提现)")
    private Integer transactionType;

    /** 交易时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交易时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date transactionTime;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 查询参数 */
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setTransactionId(Long transactionId) 
    {
        this.transactionId = transactionId;
    }

    public Long getTransactionId() 
    {
        return transactionId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) 
    {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getTransactionAmount() 
    {
        return transactionAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount) 
    {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getIncomeAmount() 
    {
        return incomeAmount;
    }

    public void setExpenseAmount(BigDecimal expenseAmount) 
    {
        this.expenseAmount = expenseAmount;
    }

    public BigDecimal getExpenseAmount() 
    {
        return expenseAmount;
    }

    public void setPaymentMethod(Integer paymentMethod) 
    {
        this.paymentMethod = paymentMethod;
    }

    public Integer getPaymentMethod() 
    {
        return paymentMethod;
    }

    public void setTransactionType(Integer transactionType) 
    {
        this.transactionType = transactionType;
    }

    public Integer getTransactionType() 
    {
        return transactionType;
    }

    public void setTransactionTime(Date transactionTime) 
    {
        this.transactionTime = transactionTime;
    }

    public Date getTransactionTime() 
    {
        return transactionTime;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("transactionId", getTransactionId())
            .append("userId", getUserId())
            .append("username", getUsername())
            .append("transactionAmount", getTransactionAmount())
            .append("incomeAmount", getIncomeAmount())
            .append("expenseAmount", getExpenseAmount())
            .append("paymentMethod", getPaymentMethod())
            .append("transactionType", getTransactionType())
            .append("transactionTime", getTransactionTime())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

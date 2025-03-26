package com.ruoyi.alse.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 支付通道配置对象 alse_pay_channel_config
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public class AlsePayChannelConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long id;

    /** 支付通道代码(zfb/vx/sqb) */
    @Excel(name = "支付通道代码(zfb/vx/sqb)")
    private String channelCode;

    /** 支付通道名称 */
    @Excel(name = "支付通道名称")
    private String channelName;

    /** 商户号 */
    @Excel(name = "商户号")
    private String merchantId;

    /** 商户名称 */
    @Excel(name = "商户名称")
    private String merchantName;

    /** 通道权重 */
    @Excel(name = "通道权重")
    private Long weight;

    /** 支付超时时间(秒) */
    @Excel(name = "支付超时时间(秒)")
    private Long timeout;

    /** 最大并发待支付订单数 */
    @Excel(name = "最大并发待支付订单数")
    private Long concurrentLimit;

    /** 是否启用(0-禁用 1-启用) */
    @Excel(name = "是否启用(0-禁用 1-启用)")
    private Integer enabled;

    /** 通道参数配置(JSON格式) */
    @Excel(name = "通道参数配置(JSON格式)")
    private String channelParams;

    /** 状态(0-正常 1-停用) */
    @Excel(name = "状态(0-正常 1-停用)")
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setChannelCode(String channelCode) 
    {
        this.channelCode = channelCode;
    }

    public String getChannelCode() 
    {
        return channelCode;
    }

    public void setChannelName(String channelName) 
    {
        this.channelName = channelName;
    }

    public String getChannelName() 
    {
        return channelName;
    }

    public void setMerchantId(String merchantId) 
    {
        this.merchantId = merchantId;
    }

    public String getMerchantId() 
    {
        return merchantId;
    }

    public void setMerchantName(String merchantName) 
    {
        this.merchantName = merchantName;
    }

    public String getMerchantName() 
    {
        return merchantName;
    }

    public void setWeight(Long weight) 
    {
        this.weight = weight;
    }

    public Long getWeight() 
    {
        return weight;
    }

    public void setTimeout(Long timeout) 
    {
        this.timeout = timeout;
    }

    public Long getTimeout() 
    {
        return timeout;
    }

    public void setConcurrentLimit(Long concurrentLimit) 
    {
        this.concurrentLimit = concurrentLimit;
    }

    public Long getConcurrentLimit() 
    {
        return concurrentLimit;
    }

    public void setEnabled(Integer enabled) 
    {
        this.enabled = enabled;
    }

    public Integer getEnabled() 
    {
        return enabled;
    }

    public void setChannelParams(String channelParams) 
    {
        this.channelParams = channelParams;
    }

    public String getChannelParams() 
    {
        return channelParams;
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
            .append("id", getId())
            .append("channelCode", getChannelCode())
            .append("channelName", getChannelName())
            .append("merchantId", getMerchantId())
            .append("merchantName", getMerchantName())
            .append("weight", getWeight())
            .append("timeout", getTimeout())
            .append("concurrentLimit", getConcurrentLimit())
            .append("enabled", getEnabled())
            .append("channelParams", getChannelParams())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

package com.ruoyi.uni.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * 收钱吧支付配置
 */
@Component
@ConfigurationProperties(prefix = "shouqianba")
@Data
public class ShouqianbaProperties {

    /**
     * API域名地址
     */
    private String apiDomain;

    /**
     * 服务商序列号
     */
    private String vendorSn;

    /**
     * 服务商密钥
     */
    private String vendorKey;

    /**
     * 应用编号
     */
    private String appId;

    /**
     * 激活码
     */
    private String code;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 客户终端编号
     */
    private String clientSn;

    /**
     * 终端名称
     */
    private String terminalName;

    /**
     * 操作员名称
     */
    private String operator;
}
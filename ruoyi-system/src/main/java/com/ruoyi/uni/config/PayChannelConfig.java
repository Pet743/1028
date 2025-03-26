package com.ruoyi.uni.config;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class PayChannelConfig {
    /**
     * 配置ID
     */
    private Long id;

    /**
     * 支付通道代码
     */
    private String channelCode;

    /**
     * 支付通道名称
     */
    private String channelName;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 通道权重
     */
    private Integer weight;

    /**
     * 支付超时时间(秒)
     */
    private Integer timeout;

    /**
     * 最大并发待支付订单数
     */
    private Integer concurrentLimit;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 通道参数配置(JSON格式)
     */
    private String params;

    /**
     * 备注
     */
    private String remark;

    /**
     * 通道参数Map(非数据库字段)
     */
    private Map<String, String> paramMap;

    /**
     * 当前待支付订单数(非数据库字段)
     */
    private AtomicInteger pendingCount = new AtomicInteger(0);

    /**
     * 判断通道是否可用
     */
    public boolean isAvailable() {
        return enabled && pendingCount.get() < concurrentLimit;
    }

    /**
     * 增加待支付订单数
     */
    public boolean incrementPending() {
        while (true) {
            int current = pendingCount.get();
            if (current >= concurrentLimit) {
                return false;
            }
            if (pendingCount.compareAndSet(current, current + 1)) {
                return true;
            }
        }
    }

    /**
     * 减少待支付订单数
     */
    public void decrementPending() {
        pendingCount.decrementAndGet();
    }
}
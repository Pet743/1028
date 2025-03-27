package com.ruoyi.uni.config;

import com.ruoyi.common.core.redis.RedisCache;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.TimeUnit;
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
     * 当前待支付订单数Redis键前缀
     */
    private static final String PENDING_COUNT_KEY_PREFIX = "pay:channel:pending:";

    /**
     * Redis客户端
     */
    @Autowired(required = false)
    private static RedisCache redisCache;

    /**
     * 设置Redis缓存
     */
    public static void setRedisCache(RedisCache cache) {
        redisCache = cache;
    }

    /**
     * 获取Redis中的待支付订单数
     */
    public int getPendingCount() {
        if (redisCache == null || id == null) {
            return pendingCount.get();
        }
        Integer count = redisCache.getCacheObject(getPendingCountKey());
        return count != null ? count : 0;
    }

    /**
     * 判断通道是否可用
     */
    public boolean isAvailable() {
        return enabled && getPendingCount() < concurrentLimit;
    }

    /**
     * 增加待支付订单数
     */
    public boolean incrementPending() {
        // 本地内存计数先增加
        while (true) {
            int current = pendingCount.get();
            if (current >= concurrentLimit) {
                return false;
            }
            if (pendingCount.compareAndSet(current, current + 1)) {
                break;
            }
        }

        // Redis中的计数也要同步增加
        if (redisCache != null && id != null) {
            try {
                // 使用Redis的原子操作递增并获取结果
                Long newCount = redisCache.increment(getPendingCountKey(), 1L);

                // 检查是否超过限制
                if (newCount > concurrentLimit) {
                    // 超过限制，回滚并减1
                    redisCache.decrement(getPendingCountKey(), 1L);
                    // 本地内存计数也减1
                    pendingCount.decrementAndGet();
                    return false;
                }

                // 设置过期时间，防止通道资源长期被占用
                redisCache.expire(getPendingCountKey(), timeout + 60, TimeUnit.SECONDS);
                return true;
            } catch (Exception e) {
                // Redis操作失败，回滚内存计数
                pendingCount.decrementAndGet();
                return false;
            }
        }

        return true;
    }

    /**
     * 减少待支付订单数
     */
    public void decrementPending() {
        // 本地内存计数减少
        pendingCount.decrementAndGet();

        // Redis中的计数也要同步减少
        if (redisCache != null && id != null) {
            try {
                redisCache.decrement(getPendingCountKey(), 1L);
            } catch (Exception e) {
                // Redis操作失败，记录日志但不影响处理流程
            }
        }
    }

    /**
     * 获取Redis中待支付订单数的键
     */
    private String getPendingCountKey() {
        return PENDING_COUNT_KEY_PREFIX + id;
    }


}
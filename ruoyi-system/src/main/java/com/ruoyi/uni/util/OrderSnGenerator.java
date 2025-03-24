package com.ruoyi.uni.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 订单号生成工具类
 */
public class OrderSnGenerator {


    /**
     * 生成带前缀的商户系统订单号
     *
     * @param prefix 自定义前缀，替代默认的"S"
     * @return 商户系统订单号
     */
    public static String generateClientSn(String prefix) {
        // 获取当前日期，格式化为yyyyMMdd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());

        // 获取当前时间戳（毫秒）
        long timestamp = System.currentTimeMillis();

        // 拼接订单号
        return prefix + dateStr + timestamp;
    }
}
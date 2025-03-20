package com.ruoyi.uni.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


/**
 * 微信支付服务接口
 */
public interface WechatPayService {

    /**
     * 创建微信支付订单
     *
     * @param outTradeNo 商户订单号
     * @param subject 商品标题
     * @param totalAmount 订单金额
     * @return 支付参数
     */
    Map<String, Object> createOrder(String outTradeNo, String subject, BigDecimal totalAmount);

    /**
     * 生成扫码支付二维码URL
     *
     * @param outTradeNo 商户订单号
     * @param subject 商品标题
     * @param totalAmount 订单金额
     * @return 支付二维码链接
     */
    String createNativePayOrder(String outTradeNo, String subject, BigDecimal totalAmount);

    /**
     * 创建微信H5支付订单（用于手机浏览器）
     *
     * @param outTradeNo 商户订单号
     * @param subject 商品标题
     * @param totalAmount 订单金额
     * @param ip 客户端IP
     * @return 支付链接地址
     */
    String createH5PayOrder(String outTradeNo, String subject, BigDecimal totalAmount, String ip);

    /**
     * 创建微信JSAPI支付订单（用于微信内浏览器）
     *
     * @param outTradeNo 商户订单号
     * @param subject 商品标题
     * @param totalAmount 订单金额
     * @param openId 用户openId
     * @return 支付参数
     */
    Map<String, String> createJsapiPayOrder(String outTradeNo, String subject, BigDecimal totalAmount, String openId);

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号
     * @return 订单状态
     */
    Map<String, Object> queryOrder(String outTradeNo);

    /**
     * 关闭订单
     *
     * @param outTradeNo 商户订单号
     * @return 是否成功
     */
    boolean closeOrder(String outTradeNo);

    /**
     * 申请退款
     *
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款单号
     * @param totalAmount 订单金额
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @return 退款结果
     */
    Map<String, Object> refund(String outTradeNo, String outRefundNo, BigDecimal totalAmount,
                               BigDecimal refundAmount, String reason);

    /**
     * 验证支付结果通知
     *
     * @param notifyData 通知数据
     * @return 验证结果，包含处理后的订单数据
     */
    Map<String, Object> verifyPayNotify(String notifyData);

    /**
     * 验证退款结果通知
     *
     * @param notifyData 通知数据
     * @return 验证结果，包含处理后的退款数据
     */
    Map<String, Object> verifyRefundNotify(String notifyData);
}
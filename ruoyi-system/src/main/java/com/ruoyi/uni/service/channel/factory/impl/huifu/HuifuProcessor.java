package com.ruoyi.uni.service.channel.factory.impl.huifu;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huifu.bspay.sdk.opps.core.BasePay;
import com.huifu.bspay.sdk.opps.core.config.MerConfig;
import com.huifu.bspay.sdk.opps.core.net.BasePayRequest;
import com.ruoyi.alse.domain.PaymentResultDTO;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.uni.config.PayChannelConfig;
import com.ruoyi.uni.model.Enum.PayChannelEnum;
import com.ruoyi.uni.service.channel.factory.PayProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class HuifuProcessor implements PayProcessor {

    @Autowired
    private HuifuConfigLoader huifuConfigLoader;

    @Override
    public PayChannelEnum getSupportedChannel() {
        return PayChannelEnum.HUIFU_MINIAPP;
    }


    @Override
    public PaymentResultDTO processPayment(BigDecimal amount, String orderNo, PayChannelConfig channelConfig, String remark) {
        log.info("处理汇付微信小程序支付，订单号: {}, 商户号: {}", orderNo, channelConfig.getMerchantId());

        try {
            // 从通道配置中获取参数
            Map<String, String> params = channelConfig.getParamMap();
            if (params == null || params.isEmpty()) {
                throw new ServiceException("汇付通道参数配置为空");
            }

            // 提取汇付配置参数
            String sysId = params.get("sysId");
            String productId = params.get("productId");
            String huifuId = params.get("huifuId");
            String privateKey = params.get("privateKey");
            String publicKey = params.get("publicKey");
            String notifyUrl = params.getOrDefault("notifyUrl", "https://callback.service.com/xx");

            log.info("使用汇付通道参数: sysId={}, huifuId={}", sysId, huifuId);

            // 初始化汇付配置
            MerConfig merConfig = new MerConfig();
            merConfig.setProcutId(productId);
            merConfig.setSysId(sysId);
            merConfig.setRsaPrivateKey(privateKey);
            merConfig.setRsaPublicKey(publicKey);
            BasePay.initWithMerConfig(merConfig);

            // 组装请求参数
            Map<String, Object> paramsInfo = new HashMap<>();

            // 预下单类型 - 微信小程序支付
            paramsInfo.put("pre_order_type", "3");
            // 请求日期
            paramsInfo.put("req_date", new SimpleDateFormat("yyyyMMdd").format(new Date()));
            // 请求流水号
            paramsInfo.put("req_seq_id", orderNo);
            // 商户号
            paramsInfo.put("huifu_id", huifuId);
            // 交易金额
            paramsInfo.put("trans_amt", amount.toPlainString());
            // 商品描述
            paramsInfo.put("goods_desc", orderNo);
            // 微信小程序扩展参数集合
            paramsInfo.put("miniapp_data", "{\"seq_id\":\"\",\"private_info\":\"oppsHosting://\",\"\":\"Y\"}");
            // 交易异步通知地址
            paramsInfo.put("notify_url", notifyUrl);

            // 发送请求到汇付API
            Map<String, Object> responseStr = BasePayRequest.requestBasePay("v2/trade/hosting/payment/preorder", paramsInfo, null, false);

            log.info("汇付响应: {}", JSON.toJSONString(responseStr));

            // 检查响应结果
            if (!"00000000".equals(responseStr.get("resp_code"))) {
                log.error("汇付下单失败: {}", responseStr.get("resp_desc"));
                throw new ServiceException("汇付下单失败: " + responseStr.get("resp_desc"));
            }

            // 获取预下单ID和小程序数据
            String preOrderId = String.valueOf(responseStr.get("pre_order_id"));
            String miniappDataStr = String.valueOf(responseStr.get("miniapp_data"));
            JSONObject miniappData = JSON.parseObject(miniappDataStr);

            // 构建支付结果
            PaymentResultDTO resultDTO = new PaymentResultDTO();
            resultDTO.setOrderNo(orderNo);
            resultDTO.setPaymentMethod(PayChannelEnum.HUIFU_MINIAPP.getCode());
            resultDTO.setTotalAmount(amount);
            resultDTO.setPaymentStatus(1); // 待支付

            // 将小程序支付数据转为JSON字符串作为结果
            JSONObject paymentData = new JSONObject();
            paymentData.put("pre_order_id", preOrderId);
            paymentData.put("miniapp_data", miniappData);

            if (miniappData.containsKey("scheme_code")) {
                paymentData.put("scheme_code", miniappData.getString("scheme_code"));
            }

            resultDTO.setPaymentUrl(paymentData.toJSONString());
            resultDTO.setRedirectType(3); // 小程序支付
            resultDTO.setChannelId(channelConfig.getMerchantId());

            return resultDTO;
        } catch (Exception e) {
            log.error("创建汇付微信小程序支付订单失败", e);
            throw new ServiceException("创建汇付微信小程序支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public boolean handlePaymentCallback(Map<String, String> params) {
        String orderNo = getOrderNoFromCallback(params);
        log.info("汇付支付回调，订单号: {}", orderNo);

        try {
            // 验证签名
            boolean signVerified = verifySignature(params);
            if (!signVerified) {
                log.error("汇付回调签名验证失败");
                return false;
            }

            // 获取交易状态
            String tradeStatus = params.get("trade_stat");

            // 判断交易是否成功
            boolean success = "1".equals(tradeStatus); // 假设1表示交易成功

            // 处理其他业务逻辑...

            return success;
        } catch (Exception e) {
            log.error("处理汇付回调失败", e);
            return false;
        }
    }

    /**
     * 验证汇付回调签名
     */
    private boolean verifySignature(Map<String, String> params) {
        try {
            // 获取订单号
            String orderNo = getOrderNoFromCallback(params);

            // 获取通道配置
            PayChannelConfig channelConfig = huifuConfigLoader.getConfigByOrderNo(orderNo);
            if (channelConfig == null) {
                log.error("未找到订单对应的支付通道配置");
                return false;
            }

            // 获取参数
            Map<String, String> channelParams = channelConfig.getParamMap();
            String publicKey = channelParams.get("publicKey");

            // 获取签名
            String sign = params.get("sign");

            // 将params按照字典顺序排序并生成待验证字符串
            // ... 实现汇付的签名验证逻辑 ...

            // 这里简化处理，实际需要根据汇付的签名规则实现
            return true;
        } catch (Exception e) {
            log.error("验证汇付回调签名异常", e);
            return false;
        }
    }

    @Override
    public String getOrderNoFromCallback(Map<String, String> params) {
        // 根据汇付的回调参数格式获取订单号
        return params.get("req_seq_id");
    }
}
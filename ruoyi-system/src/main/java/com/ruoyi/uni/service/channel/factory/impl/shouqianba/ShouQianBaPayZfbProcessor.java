package com.ruoyi.uni.service.channel.factory.impl.shouqianba;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.alse.domain.PaymentResultDTO;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.config.PayChannelConfig;
import com.ruoyi.uni.model.Enum.OrderStatusEnum;
import com.ruoyi.uni.model.Enum.PayChannelEnum;
import com.ruoyi.uni.service.channel.factory.PayProcessor;
import com.ruoyi.uni.service.shouqianba.ShouqianbaService;
import com.ruoyi.uni.util.FinanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class ShouQianBaPayZfbProcessor implements PayProcessor {

    @Autowired
    private ShouqianbaService shouqianbaService;

    @Override
    public PayChannelEnum getSupportedChannel() {
        return PayChannelEnum.SHOUQIANBA_ZFB;
    }

    @Override
    public PaymentResultDTO processPayment(BigDecimal amount, String orderNo, PayChannelConfig channelConfig, String remark) {
        log.info("处理收钱吧微信支付，订单号: {}, 商户号: {}", orderNo, channelConfig.getMerchantId());

        try {
            // 从通道配置中获取参数
            Map<String, String> params = channelConfig.getParamMap();

            // 转换金额为分
            long amountInCents = amount
                    .multiply(FinanceUtils.HUNDRED)
                    .longValue();

            // 支付方式 - 微信
            String payway = PayChannelEnum.SHOUQIANBA_ZFB.getValue(); // 微信支付的编码

            // 调用收钱吧服务创建预支付订单
            String result = shouqianbaService.precreate(
                    String.valueOf(amountInCents),
                    payway,
                    orderNo
            );

            if (StringUtils.isEmpty(result)) {
                throw new ServiceException("调用收钱吧接口失败，返回为空");
            }

            // 解析预下单结果
            JSONObject response = JSONObject.parseObject(result);

            // 检查结果码
            if (!"200".equals(response.getString("result_code"))) {
                String errorMsg = response.getString("error_message");
                log.error("收钱吧预下单失败: {}", errorMsg);
                throw new ServiceException("收钱吧预下单失败: " + errorMsg);
            }

            // 获取业务响应
            JSONObject bizResponse = response.getJSONObject("biz_response");
            if (bizResponse == null || !"PRECREATE_SUCCESS".equals(bizResponse.getString("result_code"))) {
                String errorMsg = bizResponse != null ? bizResponse.getString("error_message") : "预下单失败";
                log.error("收钱吧预下单业务失败: {}", errorMsg);
                throw new ServiceException("收钱吧预下单业务失败: " + errorMsg);
            }

            // 获取数据部分
            JSONObject data = bizResponse.getJSONObject("data");
            if (data == null) {
                throw new ServiceException("收钱吧预下单响应数据为空");
            }

            // 获取支付二维码链接
            String qrCode = data.getString("qr_code");
            if (StringUtils.isEmpty(qrCode)) {
                throw new ServiceException("收钱吧预下单未返回支付链接");
            }

            // 构建支付结果
            PaymentResultDTO resultDTO = new PaymentResultDTO();
            resultDTO.setOrderNo(orderNo);
            resultDTO.setPaymentMethod(PayChannelEnum.SHOUQIANBA_ZFB.getCode());
            resultDTO.setTotalAmount(amount);
            resultDTO.setPaymentStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
            resultDTO.setPaymentUrl(qrCode);
            resultDTO.setRedirectType(1); // 链接跳转
            resultDTO.setChannelId(channelConfig.getMerchantId());

            log.info("收钱吧微信支付预下单成功，订单号: {}, 支付链接: {}", orderNo, qrCode);
            return resultDTO;

        } catch (Exception e) {
            log.error("处理收钱吧微信支付失败", e);
            throw new ServiceException("创建收钱吧微信支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public boolean handlePaymentCallback(Map<String, String> params) {
        log.info("收到收钱吧支付回调: {}", params);

        try {
            // 获取订单号
            String orderNo = getOrderNoFromCallback(params);
            if (StringUtils.isEmpty(orderNo)) {
                log.error("回调参数缺少订单号");
                return false;
            }

            // 获取交易状态
            String tradeStatus = params.get("status");
            log.info("收钱吧回调，订单号: {}, 交易状态: {}", orderNo, tradeStatus);

            // 验证签名
            boolean signValid = verifySignature(params);
            if (!signValid) {
                log.error("收钱吧回调签名验证失败");
                return false;
            }

            // 判断交易是否成功 (SUCCESS: 支付成功)
            boolean success = "SUCCESS".equals(tradeStatus);

            // 处理业务逻辑...

            return success;
        } catch (Exception e) {
            log.error("处理收钱吧支付回调异常", e);
            return false;
        }
    }

    @Override
    public String getOrderNoFromCallback(Map<String, String> params) {
        // 根据收钱吧回调参数格式获取订单号
        return params.get("terminal_sn");
    }

    /**
     * 验证收钱吧回调签名
     */
    private boolean verifySignature(Map<String, String> params) {
        // 实现收钱吧签名验证逻辑
        // 由于收钱吧签名验证可能比较复杂，这里简化处理
        try {
            // TODO: 实现实际的签名验证逻辑
            // 参考收钱吧文档实现签名验证
            return true;
        } catch (Exception e) {
            log.error("验证收钱吧回调签名异常", e);
            return false;
        }
    }
}

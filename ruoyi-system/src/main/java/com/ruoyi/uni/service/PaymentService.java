package com.ruoyi.uni.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.ruoyi.alse.service.IAlseOrderService;
import com.ruoyi.uni.config.AlipayProperties;
import com.ruoyi.uni.converter.PaymentConverter;

import com.ruoyi.uni.model.DTO.request.payment.PaymentRequestDTO;
import com.ruoyi.uni.util.FinanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private AlipayProperties alipayProperties;


    @Autowired
    private IAlseOrderService orderService;


    /**
     * 查询支付宝订单支付状态
     *
     * @param outTradeNo 商户订单号
     * @return 支付宝响应
     * @throws AlipayApiException 调用支付宝接口异常时抛出
     */
    public AlipayTradeQueryResponse queryAlipayOrderStatus(String outTradeNo) throws AlipayApiException {
        log.info("开始查询支付宝订单状态，商户订单号：{}", outTradeNo);

        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        // 设置订单支付时传入的商户订单号
        model.setOutTradeNo(outTradeNo);

        request.setBizModel(model);

        // 执行查询
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        log.info("支付宝订单查询结果：{}", response.getBody());

        return response;
    }

    /**
     * 处理支付请求并返回支付URL：
     * 1. 先将前端传入的 PaymentRequestDTO 转换为业务模型
     * 2. 使用 FinanceUtils 对金额进行规范化处理
     * 3. 从配置文件中读取支付宝参数构造 AlipayConfig
     * 4. 构造支付宝支付请求并执行，返回支付URL
     *
     * @param requestDTO 前端传入的支付请求数据
     * @return 支付宝收银台URL，可以直接在浏览器打开
     * @throws AlipayApiException 调用支付宝接口异常时抛出
     */
    public String processPaymentReturnUrl(PaymentRequestDTO requestDTO) throws AlipayApiException {
        // 1. DTO 转换为业务模型
        AlipayTradeWapPayModel paymentModel = PaymentConverter.convertToPaymentModel(requestDTO);
        if (paymentModel == null) {
            throw new IllegalArgumentException("支付请求数据不能为空");
        }
        log.info("支付信息：" + paymentModel);

        // 2. 使用 FinanceUtils 规范化总金额，并转换为字符串
        BigDecimal normalizedAmount = FinanceUtils.normalizeAmount(new BigDecimal(paymentModel.getTotalAmount()));
        String totalAmountStr = normalizedAmount.toPlainString();

        // 3. 初始化支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 4. 构造支付请求 - 使用手机网站支付（而非PC网页支付）
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();

        // 设置回调地址
        request.setReturnUrl(alipayProperties.getReturnUrl());
        request.setNotifyUrl(alipayProperties.getNotifyUrl());
        orderService.createVirtualOrder(paymentModel.getOutTradeNo(), FinanceUtils.toBigDecimal(paymentModel.getTotalAmount()));

        // 构造业务参数JSON
        String bizContent = String.format(
                "{\"out_trade_no\":\"%s\",\"product_code\":\"QUICK_WAP_WAY\",\"subject\":\"%s\",\"total_amount\":\"%s\"}",
                paymentModel.getOutTradeNo(),
                paymentModel.getSubject(),
                totalAmountStr
        );
        request.setBizContent(bizContent);

        // 直接使用SDK执行请求并获取完整URL
        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "GET");

        // 检查响应
        if (!response.isSuccess()) {
            log.error("调用支付宝接口失败，错误码：{}, 错误描述：{}", response.getCode(), response.getMsg());
            throw new AlipayApiException("调用支付宝接口失败：" + response.getMsg());
        }

        // 获取返回的URL
        String redirectUrl = response.getBody();
        log.info("生成支付URL：{}", redirectUrl);

        return redirectUrl;
    }


    /**
     * 从配置文件中读取参数，构造 AlipayConfig 对象
     */
    private AlipayConfig getAlipayConfig() {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(alipayProperties.getServerUrl());
        alipayConfig.setAppId(alipayProperties.getAppId());
        alipayConfig.setPrivateKey(alipayProperties.getPrivateKey());
        alipayConfig.setFormat(alipayProperties.getFormat());
        alipayConfig.setAlipayPublicKey(alipayProperties.getAlipayPublicKey());
        alipayConfig.setCharset(alipayProperties.getCharset());
        alipayConfig.setSignType(alipayProperties.getSignType());
        return alipayConfig;
    }
}

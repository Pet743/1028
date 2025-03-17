package com.ruoyi.uni.converter;

import com.alipay.api.domain.*;
import com.ruoyi.uni.model.DTO.request.payment.PaymentRequestDTO;
import com.ruoyi.uni.util.FinanceUtils;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 支付订单数据转换器
 */
public class PaymentConverter {

    /**
     * PaymentRequestDTO 转换为 PaymentModel
     */
    public static AlipayTradeWapPayModel convertToPaymentModel(PaymentRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();

        // 自动生成唯一订单号：使用当前时间戳拼接一个4位随机数
        String outTradeNo = System.currentTimeMillis() + "" + (1000 + (int)(Math.random() * 9000));
        model.setOutTradeNo(outTradeNo);

        // 订单金额：使用 FinanceUtils 将字符串转换为 BigDecimal 并规范化（两位小数）
        BigDecimal totalAmount = FinanceUtils.toBigDecimal(dto.getTotalMoney());
        totalAmount = FinanceUtils.normalizeAmount(totalAmount);
        model.setTotalAmount(totalAmount.toPlainString());

        // 订单标题：使用订单号
        model.setSubject(outTradeNo);

        // 产品码：手机网站支付产品码固定为 "QUICK_WAP_WAY"
        model.setProductCode("QUICK_WAP_WAY");

        // 可选字段：如 auth_token、quit_url、time_expire 等，可根据实际需要扩展处理，此处均保持为空
        // model.setAuthToken(null);
         model.setQuitUrl("http://www.taobao.com/product/113714.html");
        // model.setTimeExpire(null);
        // model.setBusinessParams(null);
        // model.setPassbackParams(null);
        // model.setMerchantOrderNo(null);
        // model.setExtendParams(null);
        // model.setExtUserInfo(null);
        // model.setGoodsDetail(null);

        return model;
    }
}

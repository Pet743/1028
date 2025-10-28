package com.ruoyi.uni.controller;


import com.ruoyi.alse.domain.PaymentResultDTO;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.uni.model.DTO.request.payment.PayRequestDTO;
import com.ruoyi.uni.service.channel.UniPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 统一支付创建接口
 */
@RestController
@Slf4j
@RequestMapping("/api/payment")
@Api(tags = "统一支付接口")
public class UniPaymentController {

    @Autowired
    private UniPayService uniPayService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    @CheckToken
    @ApiOperation("创建统一支付订单")
    public AjaxResult createPayment(@RequestBody @Validated PayRequestDTO requestDTO) {
        try {
            log.info("收到支付请求: {}", requestDTO);

            // 将字符串金额转换为BigDecimal
            BigDecimal amount;
            try {
                amount = new BigDecimal(requestDTO.getTotalAmount());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    return AjaxResult.error("支付金额必须大于0");
                }
            } catch (NumberFormatException e) {
                return AjaxResult.error("无效的金额格式");
            }

            // 获取支付方式并校验
            String channelCode = requestDTO.getFangshi();

            // 调用支付服务创建支付订单
            PaymentResultDTO paymentResult = uniPayService.createPayment(amount, channelCode, requestDTO.getRemark());


            return AjaxResult.success("创建支付订单成功", paymentResult);
        } catch (ServiceException e) {
            log.error("创建支付订单失败: {}", e.getMessage());
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建支付订单发生异常", e);
            return AjaxResult.error("系统繁忙，请稍后再试");
        }
    }
}

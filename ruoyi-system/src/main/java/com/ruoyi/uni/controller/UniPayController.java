package com.ruoyi.uni.controller;

import com.ruoyi.uni.model.DTO.request.payment.PaymentRequestDTO;
import com.ruoyi.uni.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/api/pay")
@Api(tags = "订单创建接口")
public class UniPayController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付链接（直接返回可访问的URL）
     */
    /**
     * 创建支付链接（自动重定向）
     */
    @GetMapping("/createPaymentUrl")
    @ApiOperation("创建支付链接")
    public void createPaymentUrl(
            @RequestParam String totalMoney,
            @RequestParam String sessionKey,
            HttpServletResponse response) {
        try {
            // 构建支付请求DTO
            PaymentRequestDTO requestDTO = new PaymentRequestDTO();
            requestDTO.setTotalMoney(totalMoney);
            requestDTO.setSessionKey(sessionKey);

            // 处理支付请求，获取支付链接
            String paymentUrl = paymentService.processPaymentReturnUrl(requestDTO);

            // 直接重定向到支付页面
            response.sendRedirect(paymentUrl);
        } catch (Exception e) {
            log.error("创建支付链接失败", e);
            try {
                // 发生异常时返回错误信息
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("创建支付链接失败：" + e.getMessage());
            } catch (IOException ioEx) {
                log.error("输出错误信息失败", ioEx);
            }
        }
    }
}

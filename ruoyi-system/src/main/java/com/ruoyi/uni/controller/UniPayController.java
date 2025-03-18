package com.ruoyi.uni.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.service.IAlseOrderService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.uni.config.AlipayProperties;
import com.ruoyi.uni.model.DTO.request.order.AlipayNotifyParam;
import com.ruoyi.uni.model.DTO.request.payment.PaymentRequestDTO;
import com.ruoyi.uni.model.Enum.OrderStatusEnum;
import com.ruoyi.uni.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/api/pay")
@Api(tags = "订单创建接口")
public class UniPayController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private IAlseOrderService orderService;

    @Autowired
    private AlipayProperties alipayProperties;


    /**
     * 同步订单状态
     * 查询最近半小时的所有订单，调用支付宝接口核对支付状态，并更新数据库
     */
    @GetMapping("/syncOrderStatus")
    @ApiOperation("同步订单支付状态")
    public AjaxResult syncOrderStatus() {
        log.info("开始同步订单支付状态");

        // 查询最近30分钟的待支付订单
        List<AlseOrder> recentOrders = orderService.getRecentOrders(30);
        log.info("查询到最近30分钟的待支付订单数量：{}", recentOrders.size());

        if (recentOrders.isEmpty()) {
            return AjaxResult.success("没有需要同步的订单");
        }

        // 结果汇总
        Map<String, Object> summary = new HashMap<>();
        List<Map<String, Object>> successList = new ArrayList<>();
        List<Map<String, Object>> failedList = new ArrayList<>();
        List<Map<String, Object>> pendingList = new ArrayList<>();
        List<Map<String, Object>> closedList = new ArrayList<>();
        List<Map<String, Object>> errorList = new ArrayList<>();

        // 创建线程池并行处理查询
        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(10, recentOrders.size())); // 最多10个并发

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (AlseOrder order : recentOrders) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // 查询支付宝订单状态
                    AlipayTradeQueryResponse response = paymentService.queryAlipayOrderStatus(order.getOrderNo());

                    Map<String, Object> orderResult = new HashMap<>();
                    orderResult.put("orderId", order.getOrderId());
                    orderResult.put("orderNo", order.getOrderNo());
                    orderResult.put("amount", order.getTotalAmount());
                    orderResult.put("createTime", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", order.getCreateTime()));

                    // 根据查询结果处理
                    if (response.isSuccess()) {
                        String tradeStatus = response.getTradeStatus();
                        orderResult.put("alipayStatus", tradeStatus);

                        switch (tradeStatus) {
                            case "TRADE_SUCCESS":
                            case "TRADE_FINISHED":
                                // 支付成功，更新订单状态为待发货
                                if (order.getOrderStatus() == OrderStatusEnum.PENDING_PAYMENT.getCode()) {
                                    order.setOrderStatus(OrderStatusEnum.PENDING_SHIPMENT.getCode());
                                    order.setPaymentTime(DateUtils.getNowDate());
                                    order.setUpdateTime(DateUtils.getNowDate());
                                    order.setUpdateBy("system");
                                    orderService.updateAlseOrder(order);
                                    orderResult.put("updated", true);
                                    orderResult.put("message", "订单已支付，状态已更新为待发货");
                                } else {
                                    orderResult.put("updated", false);
                                    orderResult.put("message", "订单已支付，当前状态无需更新");
                                }
                                successList.add(orderResult);
                                break;

                            case "WAIT_BUYER_PAY":
                                // 等待支付，不更新状态
                                orderResult.put("updated", false);
                                orderResult.put("message", "订单等待支付中");
                                pendingList.add(orderResult);
                                break;

                            case "TRADE_CLOSED":
                                // 交易关闭，更新订单状态为已关闭
                                if (order.getOrderStatus() == OrderStatusEnum.PENDING_PAYMENT.getCode()) {
                                    order.setOrderStatus(OrderStatusEnum.CLOSED.getCode());
                                    order.setUpdateTime(DateUtils.getNowDate());
                                    order.setUpdateBy("system");
                                    orderService.updateAlseOrder(order);
                                    orderResult.put("updated", true);
                                    orderResult.put("message", "订单已关闭，状态已更新");
                                } else {
                                    orderResult.put("updated", false);
                                    orderResult.put("message", "订单已关闭，当前状态无需更新");
                                }
                                closedList.add(orderResult);
                                break;

                            default:
                                orderResult.put("updated", false);
                                orderResult.put("message", "未知的交易状态: " + tradeStatus);
                                errorList.add(orderResult);
                        }
                    } else {
                        // 查询失败
                        orderResult.put("alipayStatus", "QUERY_FAILED");
                        orderResult.put("errorCode", response.getCode());
                        orderResult.put("errorMsg", response.getMsg());
                        orderResult.put("updated", false);
                        orderResult.put("message", "支付宝查询失败: " + response.getMsg());
                        failedList.add(orderResult);
                    }
                } catch (AlipayApiException e) {
                    log.error("查询订单状态异常，订单号: {}", order.getOrderNo(), e);
                    Map<String, Object> orderResult = new HashMap<>();
                    orderResult.put("orderId", order.getOrderId());
                    orderResult.put("orderNo", order.getOrderNo());
                    orderResult.put("amount", order.getTotalAmount());
                    orderResult.put("createTime", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", order.getCreateTime()));
                    orderResult.put("alipayStatus", "EXCEPTION");
                    orderResult.put("errorMsg", e.getMessage());
                    orderResult.put("updated", false);
                    orderResult.put("message", "查询异常: " + e.getMessage());
                    errorList.add(orderResult);
                }
            }, executor);

            futures.add(future);
        }

        // 等待所有任务完成
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("同步订单状态超时", e);
        } finally {
            executor.shutdown();
        }

        // 汇总结果
        summary.put("total", recentOrders.size());
        summary.put("success", successList.size());
        summary.put("pending", pendingList.size());
        summary.put("closed", closedList.size());
        summary.put("failed", failedList.size());
        summary.put("error", errorList.size());
        summary.put("successDetails", successList);
        summary.put("pendingDetails", pendingList);
        summary.put("closedDetails", closedList);
        summary.put("failedDetails", failedList);
        summary.put("errorDetails", errorList);

        log.info("订单状态同步完成，成功：{}，等待支付：{}，已关闭：{}，查询失败：{}，异常：{}",
                successList.size(), pendingList.size(), closedList.size(), failedList.size(), errorList.size());

        return AjaxResult.success("订单状态同步完成", summary);
    }



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




    @ApiOperation(value = "支付宝网站订单回调接口")
    @RequestMapping(value = "/alipaywapnotify", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String alipaywapnotify(HttpServletRequest request) {
        Map<String, String> params = convertRequestParamsToMap(request); // 将异步通知中收到的待验证所有参数都存放到map中
        String paramsJson = JSON.toJSONString(params);

        try {
            // 调用SDK验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayProperties.getAlipayPublicKey(),
                    alipayProperties.getCharset(), alipayProperties.getPrivateKey());
            if (signVerified) {
                log.info("支付宝回调签名认证成功");

                // 校验支付宝回调基本参数
                check(params);

                // 获取必要参数
                String outTradeNo = params.get("out_trade_no");
                BigDecimal totalAmount = new BigDecimal(params.get("total_amount"));

                // 处理业务
                AlipayNotifyParam param = buildAlipayNotifyParam(params);
                String tradeStatus = param.getTradeStatus();

                // 支付成功
                if (tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")) {
                    try {
                        // 创建虚拟订单
                        log.info("创建虚拟订单，订单号: {}, 金额: {}", outTradeNo, totalAmount);
                        AlseOrder order = orderService.createVirtualOrder(outTradeNo, totalAmount);
                        log.info("订单创建成功，订单ID: {}", order.getOrderId());
                    } catch(Exception e) {
                        log.error("创建订单失败", e);
                        // 订单处理失败也返回success，避免支付宝重复通知
                    }
                } else {
                    log.warn("支付宝交易状态非成功状态: {}, 不处理订单创建", tradeStatus);
                }
            } else {
                log.error("支付宝回调签名验证失败: {}", paramsJson);
            }
        } catch(Exception e) {
            log.error("支付宝回调业务处理报错, params: " + paramsJson, e);
        }

        // 无论处理结果如何，都返回success，避免支付宝重复通知
        return "success";
    }


    // 将request中的参数转换成Map
    private static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> retMap = new HashMap<String, String>();

        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();

        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;

            if (valLen == 1) {
                retMap.put(name, values[0]);
            } else if (valLen > 1) {
                StringBuilder sb = new StringBuilder();
                for (String val : values) {
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.toString().substring(1));
            } else {
                retMap.put(name, "");
            }
        }

        return retMap;
    }


    /**
     * 校验支付宝回调参数
     * 只检查app_id是否匹配，不检查订单是否存在
     *
     * @param params 支付宝回调参数
     * @throws AlipayApiException 参数校验失败时抛出异常
     */
    private void check(Map<String, String> params) throws AlipayApiException {
        // 1. 验证基本参数是否存在
        if (!params.containsKey("app_id") || !params.containsKey("out_trade_no") || !params.containsKey("total_amount")) {
            throw new AlipayApiException("回调参数不完整");
        }

        // 2. 验证app_id是否为该商户本身
        if (!params.get("app_id").equals(alipayProperties.getAppId())) {
            throw new AlipayApiException("app_id不一致");
        }

        // 3. 验证订单号格式是否正确
        String outTradeNo = params.get("out_trade_no");
        if (outTradeNo == null || outTradeNo.trim().isEmpty()) {
            throw new AlipayApiException("订单号为空");
        }

        // 4. 验证金额是否合理
        try {
            BigDecimal totalAmount = new BigDecimal(params.get("total_amount"));
            if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new AlipayApiException("订单金额不合理");
            }
        } catch (NumberFormatException e) {
            throw new AlipayApiException("订单金额格式错误");
        }
    }


    private AlipayNotifyParam buildAlipayNotifyParam(Map<String, String> params) {
        String json = JSON.toJSONString(params);
        return JSON.parseObject(json, AlipayNotifyParam.class);
    }

}

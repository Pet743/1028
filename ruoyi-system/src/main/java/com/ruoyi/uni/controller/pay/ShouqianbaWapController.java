package com.ruoyi.uni.controller.pay;

import com.ruoyi.alse.service.IAlseOrderService;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.config.ShouqianbaProperties;
import com.ruoyi.uni.model.DTO.request.payment.WapPayRequestDTO;
import com.ruoyi.uni.model.Enum.PaymentMethodEnum;
import com.ruoyi.uni.model.Enum.ShouqianbaPaywayEnum;
import com.ruoyi.uni.model.constants.ApiConstants;
import com.ruoyi.uni.service.shouqianba.ShouqianbaService;
import com.ruoyi.uni.util.FinanceUtils;
import com.ruoyi.uni.util.OrderSnGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.ruoyi.common.core.domain.AjaxResult;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 收钱吧WAP支付控制器
 */
@RestController
@Slf4j
@RequestMapping("/api/wap")
@Api(tags = "订单创建接口-收钱吧")
public class ShouqianbaWapController {

    @Autowired
    private ShouqianbaService shouqianbaService;

    @Autowired
    private ShouqianbaProperties properties;


    @Autowired
    private IAlseOrderService alseOrderService;


    /**
     * 直接调用支付接口并跳转
     */
    @GetMapping("/pay")
    @ApiOperation("支付并跳转")
    public RedirectView createPayPaymentWithRedirect(@Valid WapPayRequestDTO request) {
        try {
            // 校验参数
            if (StringUtils.isEmpty(request.getTotalAmount())) {
                return new RedirectView("/error?message=支付金额不能为空");
            }

            // 解析金额：从元转为分
            BigDecimal amountInYuan;
            try {
                amountInYuan = FinanceUtils.toBigDecimal(request.getTotalAmount());

                // 校验金额合法性
                if (amountInYuan.compareTo(BigDecimal.ZERO) <= 0) {
                    return new RedirectView("/error?message=金额必须大于0");
                }
            } catch (Exception e) {
                log.error("金额格式转换失败", e);
                return new RedirectView("/error?message=金额格式无效: " + request.getTotalAmount());
            }

            // 转换为分
            long amountInCents = amountInYuan
                    .multiply(FinanceUtils.HUNDRED)
                    .longValue();

            // 获取支付方式
            String payway = ShouqianbaPaywayEnum.getValueByCode(request.getFangshi());

            // 生成订单号
            String orderSn = OrderSnGenerator.generateClientSn("S");

            // 创建订单记录
            try {
                alseOrderService.createVirtualOrder(orderSn, amountInYuan, PaymentMethodEnum.SHOUQIANBA.getCode());
            } catch (Exception e) {
                log.error("创建订单记录失败", e);
                return new RedirectView("/error?message=创建订单失败: " + e.getMessage());
            }

            // 调用支付接口
            String result = shouqianbaService.precreate(
                    String.valueOf(amountInCents),
                    payway,
                    orderSn
            );

            if (StringUtils.isEmpty(result)) {
                return new RedirectView("/error?message=支付接口调用失败");
            }

            // 解析预下单结果
            try {
                com.alibaba.fastjson2.JSONObject response = com.alibaba.fastjson2.JSONObject.parseObject(result);

                // 返回预下单结果
                if ("200".equals(response.getString("result_code"))) {
                    com.alibaba.fastjson2.JSONObject bizResponse = response.getJSONObject("biz_response");

                    if (bizResponse != null && "PRECREATE_SUCCESS".equals(bizResponse.getString("result_code"))) {
                        // 预下单成功
                        com.alibaba.fastjson2.JSONObject data = bizResponse.getJSONObject("data");
                        if (data != null) {
                            String qrCode = data.getString("qr_code");

                            if (StringUtils.isNotEmpty(qrCode)) {
                                // 直接重定向到支付链接
                                return new RedirectView(qrCode);
                            } else {
                                return new RedirectView("/error?message=没有获取到支付链接");
                            }
                        }
                    } else {
                        String errorMsg = bizResponse != null ? bizResponse.getString("error_message") : "预下单失败";
                        return new RedirectView("/error?message=" + URLEncoder.encode(errorMsg, "UTF-8"));
                    }
                } else {
                    return new RedirectView("/error?message=" +
                            URLEncoder.encode(response.getString("error_message"), "UTF-8"));
                }
            } catch (Exception e) {
                log.error("解析预下单结果异常", e);
                return new RedirectView("/error?message=预下单处理异常: " + e.getMessage());
            }

            return new RedirectView("/error?message=预下单处理失败");
        } catch (Exception e) {
            log.error("预下单异常", e);
            return new RedirectView("/error?message=预下单处理异常: " + e.getMessage());
        }
    }


    /**
     * 创建WAP支付并跳转到收钱吧支付页面
     */
    @GetMapping("/wap-payment")
    @ApiOperation("WAP跳转支付")
    public RedirectView createWapPayment(@Valid WapPayRequestDTO request) {
        try {
            // 校验终端信息
            if (shouqianbaService.getTerminalSn() == null || shouqianbaService.getTerminalKey() == null) {
                log.error("终端未激活，无法创建WAP支付");
                throw new RuntimeException("终端未激活，请先激活终端");
            }

            // 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("terminal_sn", shouqianbaService.getTerminalSn());
            String orderSn = OrderSnGenerator.generateClientSn("S");
            params.put("client_sn", orderSn);

            // 解析和转换金额：从字符串元转为分
            try {
                // 使用FinanceUtils将字符串金额转为BigDecimal并规范化
                BigDecimal amountInYuan = FinanceUtils.toBigDecimal(request.getTotalAmount());

                // 校验金额合法性
                if (amountInYuan.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("金额必须大于0");
                }

                // 转换为分
                long amountInCents = amountInYuan
                        .multiply(FinanceUtils.HUNDRED)
                        .longValue();

                params.put("total_amount", String.valueOf(amountInCents));
                alseOrderService.createVirtualOrder(orderSn, amountInYuan, PaymentMethodEnum.SHOUQIANBA.getCode());
            } catch (Exception e) {
                log.error("金额格式转换失败", e);
                throw new RuntimeException("金额格式无效: " + request.getTotalAmount());
            }


            params.put("subject", orderSn);
            // 设置支付方式 - 根据传入的fangshi参数确定payway值
            String payway = ShouqianbaPaywayEnum.getValueByCode(request.getFangshi());
            params.put("payway", payway);
            params.put("operator", Constants.SUPER_ADMIN);
            params.put("notify_url", ApiConstants.BASE_API_URL + "/api/pay/shouqianba/wap/notify");
            params.put("return_url", ApiConstants.API_URL);

            // 生成签名 - 按照签名规则实现
            String sign = generateSign(params, shouqianbaService.getTerminalKey());
            params.put("sign", sign);

            // 构建跳转URL - 使用properties中的wapGateway
            StringBuilder url = new StringBuilder(properties.getWapGateway()).append("?");
            boolean isFirst = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!isFirst) {
                    url.append("&");
                }
                url.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                isFirst = false;
            }

            log.info("创建WAP支付请求: {}", url);

            // 返回重定向视图
            return new RedirectView(url.toString());
        } catch (Exception e) {
            log.error("创建WAP支付失败", e);
            // 跳转到错误页面
            return new RedirectView("/error?message=" + e.getMessage());
        }
    }

    /**
     * 按照收钱吧规则生成签名
     * 1. 筛选参数，剔除sign与sign_type
     * 2. 按照键名ASCII码值升序排序
     * 3. 拼接成 key1=value1&key2=value2 格式
     * 4. 添加密钥 &key=key值
     * 5. 计算MD5并转大写
     *
     * @param params 参数集合
     * @param key 密钥
     * @return 签名字符串
     */
    private String generateSign(Map<String, String> params, String key) {
        try {
            // 1. 筛选参数
            Map<String, String> filteredParams = new TreeMap<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!"sign".equals(entry.getKey()) && !"sign_type".equals(entry.getKey())) {
                    filteredParams.put(entry.getKey(), entry.getValue());
                }
            }

            // 2. TreeMap已经是按键排序的

            // 3. 拼接参数
            StringBuilder stringBuilder = new StringBuilder();
            boolean isFirst = true;
            for (Map.Entry<String, String> entry : filteredParams.entrySet()) {
                if (!isFirst) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                isFirst = false;
            }

            // 4. 拼接密钥
            stringBuilder.append("&key=").append(key);

            String stringToSign = stringBuilder.toString();

            // 5. 计算MD5并转大写
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(stringToSign.getBytes(StandardCharsets.UTF_8));

            StringBuilder md5Builder = new StringBuilder();
            for (byte b : bytes) {
                md5Builder.append(String.format("%02x", b));
            }

            return md5Builder.toString().toUpperCase();
        } catch (Exception e) {
            log.error("生成签名失败", e);
            throw new RuntimeException("签名生成失败", e);
        }
    }

    /**
     * WAP支付同步回调
     */
    @GetMapping("/return")
    @ResponseBody
    public AjaxResult wapPaymentReturn(@RequestParam Map<String, String> params) {
        log.info("接收到WAP支付同步回调: {}", params);

        try {
            // 验证签名
            String sign = params.get("sign");
            if (sign == null) {
                return AjaxResult.error("签名参数缺失");
            }

            // 复制参数并移除签名
            Map<String, String> verifyParams = new HashMap<>(params);
            verifyParams.remove("sign");

            // 计算签名
            String paramStr = new JSONObject(verifyParams).toString();
            String calculatedSign = shouqianbaService.getHttpProxy().getSign(paramStr + shouqianbaService.getTerminalKey());

            // 比较签名
            if (!sign.equals(calculatedSign)) {
                log.error("WAP支付同步回调签名验证失败");
                return AjaxResult.error("签名验证失败");
            }

            // 判断支付状态
            String isSuccess = params.get("is_success");
            if ("F".equals(isSuccess)) {
                String errorCode = params.get("error_code");
                String errorMessage = params.get("error_message");
                log.error("WAP支付失败: {}, {}", errorCode, errorMessage);
                return AjaxResult.error(errorMessage);
            }

            String status = params.get("status");
            if ("SUCCESS".equals(status)) {
                // 支付成功
                String sn = params.get("sn");  // 收钱吧唯一订单号
                String clientSn = params.get("client_sn");  // 商户系统订单号
                String tradeNo = params.get("trade_no");  // 支付通道交易凭证号
                String totalAmount = params.get("total_amount");  // 交易总金额(分)

                // 转换金额单位：分 -> 元
                BigDecimal amountInYuan = new BigDecimal(totalAmount)
                        .divide(FinanceUtils.HUNDRED, 2, RoundingMode.HALF_UP);

                Map<String, Object> data = new HashMap<>();
                data.put("sn", sn);
                data.put("clientSn", clientSn);
                data.put("tradeNo", tradeNo);
                data.put("totalAmount", amountInYuan);
                data.put("subject", params.get("subject"));
                data.put("reflect", params.get("reflect"));

                return AjaxResult.success("支付成功", data);
            } else if ("FAIL".equals(status)) {
                // 支付失败
                String resultCode = params.get("result_code");
                String resultMessage = params.get("result_message");
                log.error("WAP支付状态失败: {}, {}", resultCode, resultMessage);
                return AjaxResult.error(resultMessage != null ? resultMessage : "支付失败");
            } else {
                // 其他状态
                return AjaxResult.error("未知的支付状态: " + status);
            }
        } catch (Exception e) {
            log.error("处理WAP支付同步回调异常", e);
            return AjaxResult.error("处理支付回调异常: " + e.getMessage());
        }
    }

    /**
     * WAP支付异步通知
     * 注意：异步通知逻辑应与同步回调类似，但需返回纯文本的success给收钱吧服务器
     */
    @PostMapping("/notify")
    @ResponseBody
    public String wapPaymentNotify(@RequestBody String notifyData) {
        log.info("接收到WAP支付异步通知: {}", notifyData);

        try {
            // 解析JSON数据
            JSONObject params = new JSONObject(notifyData);

            // 验证签名
            String sign = params.getString("sign");
            if (sign == null) {
                log.error("异步通知签名参数缺失");
                return "fail";
            }

            // 复制参数并移除签名
            JSONObject verifyParams = new JSONObject(params.toString());
            verifyParams.remove("sign");

            // 计算签名
            String calculatedSign = shouqianbaService.getHttpProxy().getSign(
                    verifyParams.toString() + shouqianbaService.getTerminalKey());

            // 比较签名
            if (!sign.equals(calculatedSign)) {
                log.error("WAP支付异步通知签名验证失败");
                return "fail";
            }

            // 判断支付状态
            String status = params.getString("status");
            if ("SUCCESS".equals(status)) {
                // 支付成功，进行业务处理
                String sn = params.getString("sn");  // 收钱吧唯一订单号
                String clientSn = params.getString("client_sn");  // 商户系统订单号

                // TODO: 更新订单状态等业务逻辑

                log.info("WAP支付成功，订单号: {}", clientSn);
                return "success";
            } else {
                log.error("WAP支付状态异常: {}", status);
                return "fail";
            }
        } catch (Exception e) {
            log.error("处理WAP支付异步通知异常", e);
            return "fail";
        }
    }
}
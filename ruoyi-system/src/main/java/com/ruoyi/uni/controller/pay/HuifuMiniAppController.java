package com.ruoyi.uni.controller.pay;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huifu.bspay.sdk.opps.core.BasePay;
import com.huifu.bspay.sdk.opps.core.config.MerConfig;
import com.huifu.bspay.sdk.opps.core.net.BasePayRequest;
import com.ruoyi.alse.service.IAlseOrderService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.uni.config.HuifuProperties;
import com.ruoyi.uni.model.DTO.request.payment.HuifuMiniAppPayRequestDTO;
import com.ruoyi.uni.model.Enum.PayChannelEnum;
import com.ruoyi.uni.util.FinanceUtils;
import com.ruoyi.uni.util.OrderSnGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 汇付微信小程序支付控制器
 */
@RestController
@Slf4j
@RequestMapping("/api/huifu")
@Api(tags = "订单创建接口-汇付")
public class HuifuMiniAppController {

    @Autowired
    private HuifuProperties huifuProperties;

    @Autowired
    private IAlseOrderService alseOrderService;

    /**
     * 创建微信小程序支付并跳转到支付页面
     */
    @PostMapping("/miniapp/pay")
    @ApiOperation("微信小程序支付")
    public AjaxResult createMiniAppPayment(@RequestBody @Valid HuifuMiniAppPayRequestDTO request) {
        try {
            log.info("创建汇付微信小程序支付请求: {}", JSON.toJSONString(request));

            // 生成订单号
            String orderNo = OrderSnGenerator.generateClientSn("HF");

            // 解析和验证金额
            BigDecimal amount = FinanceUtils.toBigDecimal(request.getTotalAmount());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return AjaxResult.error("支付金额必须大于0");
            }
            MerConfig merConfig = new MerConfig();
            merConfig.setProcutId(huifuProperties.getProductId());
            merConfig.setSysId(huifuProperties.getSysId());
            merConfig.setRsaPrivateKey(huifuProperties.getPrivateKey());
            merConfig.setRsaPublicKey(huifuProperties.getPublicKey());
            BasePay.initWithMerConfig(merConfig);

            // 组装请求参数 - 与示例代码保持一致
            Map<String, Object> paramsInfo = new HashMap<>();

            // 预下单类型
            paramsInfo.put("pre_order_type", "3");
            // 请求日期
            paramsInfo.put("req_date", new SimpleDateFormat("yyyyMMdd").format(new Date()));
            // 请求流水号
            paramsInfo.put("req_seq_id", orderNo);
            // 商户号
            paramsInfo.put("huifu_id", huifuProperties.getHuifuId());
            // 交易金额
            paramsInfo.put("trans_amt", amount.toPlainString());
            // 商品描述
            paramsInfo.put("goods_desc", orderNo);
            // 微信小程序扩展参数集合
            paramsInfo.put("miniapp_data", "{\"seq_id\":\"\",\"private_info\":\"oppsHosting://\",\"\":\"Y\"}");
            // 是否延迟交易
            paramsInfo.put("delay_acct_flag", "Y");
            // 分账对象
//            paramsInfo.put("acct_split_bunch", "{\"acct_infos\":[{\"div_amt\":\"0.01\",\"huifu_id\":\"6666000109133323\"}]}");
            // 交易异步通知地址
            paramsInfo.put("notify_url", "https://callback.service.com/xx");

            // 将Map转换为JSON对象用于排序
            JSONObject reqData = new JSONObject(paramsInfo);

            // 按照参数字典顺序排序
            String sortedDataJson = sortJsonObject(reqData);
            log.info("排序后的请求数据: {}", sortedDataJson);

            // 对排序后的数据进行签名
            String sign = sign(sortedDataJson, huifuProperties.getPrivateKey());

            // 构建完整请求
            JSONObject fullRequest = new JSONObject();
            fullRequest.put("sys_id", huifuProperties.getSysId());
            fullRequest.put("product_id", huifuProperties.getProductId());
            fullRequest.put("data", reqData);
            fullRequest.put("sign", sign);

            log.info("向汇付发送请求: {}", fullRequest.toJSONString());

            // 2. 发送请求到汇付API
            Map<String, Object> responseStr = BasePayRequest.requestBasePay("v2/trade/hosting/payment/preorder", paramsInfo, null, false);

            // 异步创建虚拟订单
            alseOrderService.createVirtualOrder(orderNo, amount, PayChannelEnum.HUIFU_MINIAPP.getSystem());


            log.info("汇付响应: {}", JSON.toJSONString(responseStr));

            // 直接使用responseStr这个Map获取响应数据
            if (!"00000000".equals(responseStr.get("resp_code"))) {
                log.error("汇付下单失败: {}", responseStr.get("resp_desc"));
                return AjaxResult.error(String.valueOf(responseStr.get("resp_desc")));
            }

            // 异步创建虚拟订单
            alseOrderService.createVirtualOrder(orderNo, amount, PayChannelEnum.HUIFU_MINIAPP.getSystem());

            // 获取预下单ID和小程序数据
            String preOrderId = String.valueOf(responseStr.get("pre_order_id"));
            String miniappDataStr = String.valueOf(responseStr.get("miniapp_data"));
            JSONObject miniappData = JSON.parseObject(miniappDataStr);

            // 构造返回结果
            JSONObject result = new JSONObject();
            result.put("pre_order_id", preOrderId);
            result.put("miniapp_data", miniappData);

            // 直接添加scheme_code到返回结果
            if (miniappData.containsKey("scheme_code")) {
                String schemeCode = miniappData.getString("scheme_code");
                result.put("scheme_code", schemeCode);
            }

            return AjaxResult.success("下单成功", result);
        } catch (Exception e) {
            log.error("创建汇付微信小程序支付失败", e);
            return AjaxResult.error("支付创建失败: " + e.getMessage());
        }
    }

    /**
     * 重定向到微信小程序支付
     */
    @GetMapping("/miniapp/redirect")
    public RedirectView redirectToMiniApp(@RequestParam("scheme") String scheme, @RequestParam("order_no") String orderNo) {
        log.info("重定向到微信小程序支付，订单号: {}, scheme: {}", orderNo, scheme);
        return new RedirectView(scheme);
    }

    /**
     * 汇付支付结果异步通知
     */
    @PostMapping("/notify")
    @ResponseBody
    public String paymentNotify(@RequestBody String notifyData) {
        log.info("接收到汇付支付异步通知: {}", notifyData);

        try {
            // 解析通知数据
            JSONObject notify = JSON.parseObject(notifyData);
            JSONObject respData = notify.getJSONObject("resp_data");

            // 验证签名
            String sign = notify.getString("sign");
            if (sign == null) {
                log.error("异步通知签名参数缺失");
                return "fail";
            }

            // TODO: 实现签名验证逻辑

            // 处理支付结果
            String respCode = respData.getString("resp_code");
            String orderNo = respData.getString("req_seq_id");
            String transStat = respData.getString("trans_stat");

            if ("00000000".equals(respCode) && "S".equals(transStat)) {
                // 支付成功，更新订单状态等业务逻辑
                log.info("订单支付成功，订单号: {}", orderNo);

                // TODO: 处理订单状态更新逻辑

                return "success";
            } else {
                log.error("支付失败，订单号: {}, 状态: {}", orderNo, transStat);
                return "fail";
            }
        } catch (Exception e) {
            log.error("处理支付异步通知异常", e);
            return "fail";
        }
    }


    /**
     * 对JSON对象按照参数字典顺序排序并返回JSON字符串
     * 仅对第一层参数进行排序
     */
    private String sortJsonObject(JSONObject jsonObject) {
        TreeMap<String, Object> sortedMap = new TreeMap<>();

        // 将JSON对象转入TreeMap进行自然排序
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        // 转换为JSON字符串
        return JSON.toJSONString(sortedMap);
    }

    /**
     * RSA私钥签名
     */
    private String sign(String data, String privateKeyBase64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes("UTF-8"));

            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new RuntimeException("签名失败", e);
        }
    }
}
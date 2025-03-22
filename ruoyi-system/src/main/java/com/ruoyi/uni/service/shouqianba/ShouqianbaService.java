package com.ruoyi.uni.service.shouqianba;

import com.ruoyi.uni.config.ShouqianbaProperties;
import com.ruoyi.uni.util.HttpProxy;
import com.ruoyi.uni.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 收钱吧支付服务
 */
@Service
public class ShouqianbaService {

    private static final Logger log = LoggerFactory.getLogger(ShouqianbaService.class);

    @Autowired
    private ShouqianbaProperties properties;

    private HttpProxy httpProxy;

    // 存储终端信息
    private String terminalSn;
    private String terminalKey;

    /**
     * 初始化HttpProxy
     */
    @Autowired
    public void init() {
        httpProxy = new HttpProxy(properties.getApiDomain());
    }

    /**
     * 获取随机订单号
     */
    public String getClientSn(int length) {
        return httpProxy.getClient_Sn(length);
    }

    /**
     * 激活终端
     * @return 是否激活成功
     */
    public boolean activate() {
        try {
            JSONObject result = httpProxy.activate(
                    properties.getVendorSn(),
                    properties.getVendorKey(),
                    properties.getAppId(),
                    properties.getCode()
            );

            if (result != null) {
                this.terminalSn = result.getString("terminal_sn");
                this.terminalKey = result.getString("terminal_key");
                log.info("终端激活成功，终端号：{}", terminalSn);
                return true;
            } else {
                log.error("终端激活失败");
                return false;
            }
        } catch (Exception e) {
            log.error("终端激活异常", e);
            return false;
        }
    }

    /**
     * 终端签到
     * @return 是否签到成功
     */
    public boolean checkin() {
        if (terminalSn == null || terminalKey == null) {
            log.error("终端未激活，无法签到");
            return false;
        }

        try {
            JSONObject result = httpProxy.checkin(terminalSn, terminalKey);

            if (result != null) {
                this.terminalSn = result.getString("terminal_sn");
                this.terminalKey = result.getString("terminal_key");
                log.info("终端签到成功，更新终端密钥");
                return true;
            } else {
                log.error("终端签到失败");
                return false;
            }
        } catch (Exception e) {
            log.error("终端签到异常", e);
            return false;
        }
    }

    /**
     * 支付接口
     * @param totalAmount 支付金额（分）
     * @param payway 支付方式 1:支付宝 3:微信 4:百付宝 5:京东钱包
     * @param dynamicId 条码内容
     * @param subject 交易简介
     * @return 支付结果
     */
    public String pay(String totalAmount, String payway, String dynamicId, String subject) {
        if (terminalSn == null || terminalKey == null) {
            log.error("终端未激活或未签到，无法支付");
            return null;
        }

        try {
            JSONObject params = new JSONObject();
            params.put("terminal_sn", terminalSn);
            params.put("client_sn", httpProxy.getClient_Sn(16));
            params.put("total_amount", totalAmount);
            params.put("payway", payway);
            params.put("dynamic_id", dynamicId);
            params.put("subject", subject);
            params.put("operator", properties.getOperator());

            String sign = httpProxy.getSign(params.toString() + terminalKey);
            String result = HttpUtil.httpPost(properties.getApiDomain() + "/upay/v2/pay",
                    params.toString(), sign, terminalSn);

            return result;
        } catch (Exception e) {
            log.error("支付请求异常", e);
            return null;
        }
    }

    /**
     * 退款接口
     * @param sn 收钱吧系统订单号
     * @param clientSn 商户系统订单号
     * @param refundAmount 退款金额
     * @param refundRequestNo 退款序列号
     * @return 退款结果
     */
    public String refund(String sn, String clientSn, String refundAmount, String refundRequestNo) {
        if (terminalSn == null || terminalKey == null) {
            log.error("终端未激活或未签到，无法退款");
            return null;
        }

        try {
            JSONObject params = new JSONObject();
            params.put("terminal_sn", terminalSn);
            params.put("sn", sn);
            params.put("client_sn", clientSn);
            params.put("refund_amount", refundAmount);
            params.put("refund_request_no", refundRequestNo);
            params.put("operator", properties.getOperator());

            String sign = httpProxy.getSign(params.toString() + terminalKey);
            String result = HttpUtil.httpPost(properties.getApiDomain() + "/upay/v2/refund",
                    params.toString(), sign, terminalSn);

            return result;
        } catch (Exception e) {
            log.error("退款请求异常", e);
            return null;
        }
    }

    /**
     * 查询接口
     * @param sn 收钱吧系统订单号
     * @param clientSn 商户系统订单号
     * @return 查询结果
     */
    public String query(String sn, String clientSn) {
        if (terminalSn == null || terminalKey == null) {
            log.error("终端未激活或未签到，无法查询");
            return null;
        }

        try {
            JSONObject params = new JSONObject();
            params.put("terminal_sn", terminalSn);
            params.put("sn", sn);
            params.put("client_sn", clientSn);

            String sign = httpProxy.getSign(params.toString() + terminalKey);
            String result = HttpUtil.httpPost(properties.getApiDomain() + "/upay/v2/query",
                    params.toString(), sign, terminalSn);

            return result;
        } catch (Exception e) {
            log.error("查询请求异常", e);
            return null;
        }
    }

    /**
     * 撤单接口
     * @param sn 收钱吧系统订单号
     * @param clientSn 商户系统订单号
     * @return 撤单结果
     */
    public String cancel(String sn, String clientSn) {
        if (terminalSn == null || terminalKey == null) {
            log.error("终端未激活或未签到，无法撤单");
            return null;
        }

        try {
            JSONObject params = new JSONObject();
            params.put("terminal_sn", terminalSn);
            params.put("sn", sn);
            params.put("client_sn", clientSn);

            String sign = httpProxy.getSign(params.toString() + terminalKey);
            String result = HttpUtil.httpPost(properties.getApiDomain() + "/upay/v2/cancel",
                    params.toString(), sign, terminalSn);

            return result;
        } catch (Exception e) {
            log.error("撤单请求异常", e);
            return null;
        }
    }

    /**
     * 预下单接口
     * @param totalAmount 支付金额（分）
     * @param payway 支付方式 1:支付宝 3:微信 4:百付宝 5:京东钱包
     * @param subject 交易简介
     * @param subPayway 二级支付方式
     * @return 预下单结果
     */
    public String precreate(String totalAmount, String payway, String subject, String subPayway) {
        if (terminalSn == null || terminalKey == null) {
            log.error("终端未激活或未签到，无法预下单");
            return null;
        }

        try {
            JSONObject params = new JSONObject();
            params.put("terminal_sn", terminalSn);
            params.put("client_sn", httpProxy.getClient_Sn(16));
            params.put("total_amount", totalAmount);
            params.put("payway", payway);
            params.put("subject", subject);
            params.put("operator", properties.getOperator());
            params.put("sub_payway", subPayway);

            String sign = httpProxy.getSign(params.toString() + terminalKey);
            String result = HttpUtil.httpPost(properties.getApiDomain() + "/upay/v2/precreate",
                    params.toString(), sign, terminalSn);

            return result;
        } catch (Exception e) {
            log.error("预下单请求异常", e);
            return null;
        }
    }

    /**
     * 获取终端号
     */
    public String getTerminalSn() {
        return terminalSn;
    }

    /**
     * 获取终端密钥
     */
    public String getTerminalKey() {
        return terminalKey;
    }

    /**
     * 设置终端号和密钥（用于从持久化存储恢复）
     */
    public void setTerminalInfo(String terminalSn, String terminalKey) {
        this.terminalSn = terminalSn;
        this.terminalKey = terminalKey;
    }
}
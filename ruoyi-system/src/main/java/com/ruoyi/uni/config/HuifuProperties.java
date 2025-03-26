package com.ruoyi.uni.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 汇付配置属性类
 */
@Component
@ConfigurationProperties(prefix = "huifu")
public class HuifuProperties {

    /**
     * 系统ID
     */
    private String sysId;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 商户号
     */
    private String huifuId;

    /**
     * 私钥(Base64编码)
     */
    private String privateKey;

    /**
     * 汇付公钥(Base64编码)
     */
    private String publicKey;

    /**
     * 微信小程序预下单接口地址
     */
    private String miniappPreorderUrl = "https://api.huifu.com/v2/trade/hosting/payment/preorder";

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    // Getter和Setter方法
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getHuifuId() {
        return huifuId;
    }

    public void setHuifuId(String huifuId) {
        this.huifuId = huifuId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getMiniappPreorderUrl() {
        return miniappPreorderUrl;
    }

    public void setMiniappPreorderUrl(String miniappPreorderUrl) {
        this.miniappPreorderUrl = miniappPreorderUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}
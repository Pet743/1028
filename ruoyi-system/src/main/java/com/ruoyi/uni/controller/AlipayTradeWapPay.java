package com.ruoyi.uni.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.domain.ExtUserInfo;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.domain.ExtendParams;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.request.AlipayTradeWapPayRequest;


import java.util.ArrayList;
import java.util.List;

public class AlipayTradeWapPay {


/*
    public static void main(String[] args) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();

        // 设置商户订单号
        model.setOutTradeNo("70501111111S001111119");

        // 设置订单总金额
        model.setTotalAmount("9.00");

        // 设置订单标题
        model.setSubject("大乐透");

        // 设置产品码
        model.setProductCode("QUICK_WAP_WAY");

        // 设置针对用户授权接口
        model.setAuthToken("appopenBb64d181d0146481ab6a762c00714cC27");

        // 设置用户付款中途退出返回商户网站的地址
        model.setQuitUrl("http://www.taobao.com/product/113714.html");

        // 设置订单包含的商品列表信息
        List<GoodsDetail> goodsDetail = new ArrayList<GoodsDetail>();
        GoodsDetail goodsDetail0 = new GoodsDetail();
        goodsDetail0.setGoodsName("ipad");
        goodsDetail0.setAlipayGoodsId("20010001");
        goodsDetail0.setQuantity(1L);
        goodsDetail0.setPrice("2000");
        goodsDetail0.setGoodsId("apple-01");
        goodsDetail0.setGoodsCategory("34543238");
        goodsDetail0.setCategoriesTree("124868003|126232002|126252004");
        goodsDetail0.setBody("特价手机");
        goodsDetail0.setShowUrl("http://www.alipay.com/xxx.jpg");
        goodsDetail.add(goodsDetail0);
        model.setGoodsDetail(goodsDetail);

        // 设置订单绝对超时时间
        model.setTimeExpire("2025-03-17 11:59:31");

        // 设置业务扩展参数
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088511833207846");
        extendParams.setHbFqSellerPercent("100");
        extendParams.setHbFqNum("3");
        extendParams.setIndustryRefluxInfo("{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}");
        extendParams.setRoyaltyFreeze("true");
        extendParams.setCardType("S0JP0000");
        model.setExtendParams(extendParams);

        // 设置商户传入业务信息
        model.setBusinessParams("{\"mc_create_trade_ip\":\"127.0.0.1\"}");

        // 设置公用回传参数
        model.setPassbackParams("merchantBizType%3d3C%26merchantBizNo%3d2016010101111");

        // 设置商户的原始订单号
        model.setMerchantOrderNo("20161008001");

        // 设置外部指定买家
        ExtUserInfo extUserInfo = new ExtUserInfo();
        extUserInfo.setCertType("IDENTITY_CARD");
        extUserInfo.setCertNo("362334768769238881");
        extUserInfo.setName("李明");
        extUserInfo.setMobile("16587658765");
        extUserInfo.setFixBuyer("F");
        extUserInfo.setMinAge("18");
        extUserInfo.setNeedCheckInfo("F");
        extUserInfo.setIdentityHash("27bfcd1dee4f22c8fe8a2374af9b660419d1361b1c207e9b41a754a113f38fcc");
        model.setExtUserInfo(extUserInfo);

        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "POST");
        // 如果需要返回GET请求，请使用
        // AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "GET");
        String pageRedirectionData = response.getBody();
        System.out.println(pageRedirectionData);

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }
    }
    String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCEr5L/cd3t9sHRBhhCx0eq8thGeqdAZyg0xP6oIGPX+4U9AC4eroKkxOCM8wv9AaF/58ckd2BVQn8FT5p4Af16bh0ytzLHd+QixdmsF92yknTstXLBvu5hDZ2F3Si3rYLwiL8BDdKAdtoyNT5bW4+G8SSHci9PjmOV3qhPm9TaPmpHiRCOkwIwIRIdiSKoMoM4J1uIOWVXbiY12dwEdCJt90pACwV9sxsHpZIF2nuv0jyH+GfdcuIRT+66jV814+m3JZZltYiWxM868KQIra+8JYV6b5N0OKOEfcNzmN+COmOyXBJmZ/8QxIPo+62zFHSBg7MEXHCEMIxKLhJYMaqXAgMBAAECggEAOzo8PbZQQFazcWBtF/HoddBs5hj962ugsN12Zwn+R3zIntAjxiJTwsT3DzO2mDtQIc4OQKLg524FUROVehg5U5svgWHxUsSbm0IBxiXpGjrx4m+kSAO4WK5BMG3mLQdQuZnw2MGYwytKfrrxmMooKS4fBi7cG4ENxupa6x4+RySMYNW/SR6NW+HTuOsl4VdrEOTKM7pnxz7adRPjgLFPFyvAwy31WLv6glAkcOz4Mxbyosvuqg0ShKGqzurSV4+TOapFD6xs6va3ljqoV9vPvoqgFAMOZXGYZqVNcLb+UJsN/+CKHN+zwEGYDPfJYmzOxf/kZV6e5iUR/Ul2vqyxwQKBgQDEcEEhFREzovju8bd5VmZCqRx5Wj8HVP4Alavz9e9Y3fes0rbfA8pTy9iBxS/KSW1qOwToh9Xy1cniS2TVBUGVhryhVnnT7//ZXV+ikVRXwYocBsjA7WZEFTnBjrtnzfeOs0QkcimUgXhkfmxaem4IksA/BKJBf7ohd5mww2568QKBgQCs6sbpGKuqyo99TKCdOLBVRzvKq4f6sH0eU/kdn/g/yJMfEqUpcgoSzd2zgDQpD0/bTH27Yi6CmdbyeUuRStpGhtsviKX0VCDyCrOek3khbCfyVBF+JU0mW+BMZjw2uSHRc/LlRz1F7b5EbYTkEKOcXu1d3ZJ8rx+oxq7g9WUuBwKBgQCcdah6WBROY4UQlDgFjfp/J8EFKws+pMR25L+D3aPYzVrjLSX4tUUmvxrgKZWp8hgS35VGPEL9Wqik9FYIkVouZZXCj2UTmHnYwFVW35glGYMOVZz6hVvJ6HoLH8tGn7eI0mH5AXD61rB0onldC1yuuWwkBWXS84/l7iPctZw+kQKBgQCXgYYTCoivNpgOwysfVmI59hYPd8fHAQg/oHfvWxCaYlV1rlKEQ+VLoJEWC8ioCUCA9z5bBqWVzNEm1tw9S4aAsPwvY910fJn5OSM0WCaUAD3n93z0OB6m821Nw24SzJcy/BLgjVDEipBhSExdv656/jmgOtl04cQtjxf/B0RF1QKBgDrWd1+f+kWQE9RXPd19vzUrKJR0b2XjHCoZ+suCu2hwdfFrBs6AIi0MBO7Zvc4IJU2UnNQaFO7ps7E7KfSJghG9OMv4Lr+ajmvSmWvttbGmbY0ibtQ3uRNG2SUzhKaiNMDiHLwDLLTtTCPQERRpEI/Sr2RwcxmnSRZ46XFZpHKA";
    String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhK+S/3Hd7fbB0QYYQsdHqvLYRnqnQGcoNMT+qCBj1/uFPQAuHq6CpMTgjPML/QGhf+fHJHdgVUJ/BU+aeAH9em4dMrcyx3fkIsXZrBfdspJ07LVywb7uYQ2dhd0ot62C8Ii/AQ3SgHbaMjU+W1uPhvEkh3IvT45jld6oT5vU2j5qR4kQjpMCMCESHYkiqDKDOCdbiDllV24mNdncBHQibfdKQAsFfbMbB6WSBdp7r9I8h/hn3XLiEU/uuo1fNePptyWWZbWIlsTPOvCkCK2vvCWFem+TdDijhH3Dc5jfgjpjslwSZmf/EMSD6PutsxR0gYOzBFxwhDCMSi4SWDGqlwIDAQAB";

*/

/*    private static AlipayConfig getAlipayConfig() {
      AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId("2021005127643793");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }*/
}
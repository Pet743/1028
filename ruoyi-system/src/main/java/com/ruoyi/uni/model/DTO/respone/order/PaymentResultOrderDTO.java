package com.ruoyi.uni.model.DTO.respone.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@ApiModel("通用支付结果响应")
public class PaymentResultOrderDTO {

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "支付方式")
    private Integer paymentMethod;

    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "支付状态(0-待支付，1-支付中，2-支付成功)")
    private Integer paymentStatus;

    @ApiModelProperty(value = "支付链接")
    private String paymentUrl;

    @ApiModelProperty(value = "支付二维码内容")
    private String qrCodeContent;

    @ApiModelProperty(value = "支付表单内容(HTML)")
    private String formContent;

    @ApiModelProperty(value = "支付方式特定参数")
    private Map<String, Object> paymentParams;

    @ApiModelProperty(value = "支付预下单ID")
    private String prepayId;

    @ApiModelProperty(value = "支付跳转方式(1-链接，2-二维码，3-表单，4-APP参数，5-无需跳转)")
    private Integer redirectType;
}
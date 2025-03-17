package com.ruoyi.uni.model.DTO.respone.order;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("订单信息")
public class OrderResponseDTO {

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商品ID")
    private Long productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片URL")
    private String productImageUrl;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "购买数量")
    private Long quantity;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "支付方式(1:支付宝 2:微信 3:银行卡 4:钱包)")
    private Integer paymentMethod;

    @ApiModelProperty(value = "支付方式描述")
    private String paymentMethodDesc;

    @ApiModelProperty(value = "订单状态(1:待付款 2:待发货 3:待收货 4:待评价 5:退款中 6:已关闭)")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单状态描述")
    private String orderStatusDesc;

    @ApiModelProperty(value = "买家ID")
    private Long buyerId;

    @ApiModelProperty(value = "买家姓名")
    private String buyerName;

    @ApiModelProperty(value = "买家电话")
    private String buyerPhone;

    @ApiModelProperty(value = "卖家ID")
    private Long sellerId;

    @ApiModelProperty(value = "卖家姓名")
    private String sellerName;

    @ApiModelProperty(value = "卖家电话")
    private String sellerPhone;

    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @ApiModelProperty(value = "收货地址")
    private String shippingAddress;

    @ApiModelProperty(value = "物流公司")
    private String logisticsCompany;

    @ApiModelProperty(value = "物流单号")
    private String logisticsNo;

    @ApiModelProperty(value = "下单时间")
    private Date createTime;

    @ApiModelProperty(value = "付款时间")
    private Date paymentTime;

    @ApiModelProperty(value = "发货时间")
    private Date shippingTime;

    @ApiModelProperty(value = "收货时间")
    private Date receivingTime;
}
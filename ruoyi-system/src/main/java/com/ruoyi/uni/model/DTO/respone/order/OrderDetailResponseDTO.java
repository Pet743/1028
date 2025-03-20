package com.ruoyi.uni.model.DTO.respone.order;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("订单详情信息")
public class OrderDetailResponseDTO extends OrderResponseDTO {

    @ApiModelProperty(value = "关联钱包流水ID")
    private Long walletTransactionId;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    // 收货地址详细信息
    @ApiModelProperty(value = "联系人姓名")
    private String contactName;

    @ApiModelProperty(value = "联系人手机号")
    private String contactPhone;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区/县")
    private String district;

    @ApiModelProperty(value = "详细地址")
    private String detailAddress;

    // 物流信息
    @ApiModelProperty(value = "物流公司")
    private String logisticsCompany;

    @ApiModelProperty(value = "物流单号")
    private String logisticsNo;

    // 交易时间信息
    @ApiModelProperty(value = "付款时间")
    private Date paymentTime;

    @ApiModelProperty(value = "发货时间")
    private Date shippingTime;

    @ApiModelProperty(value = "收货时间")
    private Date receivingTime;
}
package com.ruoyi.uni.model.DTO.request.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单列表请求")
public class OrderListRequestDTO {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "订单状态(0:全部 1:待付款 2:待发货 3:待收货 4:待评价 5:退款中 6:已关闭)", example = "0")
    private Integer orderStatus = 0;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页数量", example = "10")
    private Integer pageSize = 10;
}
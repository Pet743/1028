package com.ruoyi.uni.model.DTO.request.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("浏览记录项")
public class BrowsingHistoryItem {

    @ApiModelProperty(value = "商品ID")
    private Long productId;

    @ApiModelProperty(value = "商品标题")
    private String productTitle;

    @ApiModelProperty(value = "商品封面图片")
    private String productCoverImg;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "浏览时间")
    private Date viewTime;
}
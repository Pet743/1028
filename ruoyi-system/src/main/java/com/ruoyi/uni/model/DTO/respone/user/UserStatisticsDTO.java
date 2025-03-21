package com.ruoyi.uni.model.DTO.respone.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("用户统计信息DTO")
public class UserStatisticsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("发布的商品总数")
    private Integer publishedProductCount;

    @ApiModelProperty("卖出的商品总数")
    private Integer soldProductCount;

    @ApiModelProperty("买到的商品总数")
    private Integer boughtProductCount;

    @ApiModelProperty("钱包余额")
    private String walletBalance;

    @ApiModelProperty("收藏的商品总数")
    private Integer favoriteProductCount;

    @ApiModelProperty("浏览历史总数")
    private Integer browsingHistoryCount;

    @ApiModelProperty("关注的用户总数")
    private Integer followedUserCount;
}
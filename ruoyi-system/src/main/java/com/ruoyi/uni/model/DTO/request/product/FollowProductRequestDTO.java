package com.ruoyi.uni.model.DTO.request.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("收藏/取消收藏商品请求DTO")
public class FollowProductRequestDTO {

    @ApiModelProperty(value = "当前用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "目标商品ID", required = true)
    private Long targetProductId;

    @ApiModelProperty(value = "是否收藏（true-收藏，false-取消收藏）", required = true)
    private boolean follow;
}
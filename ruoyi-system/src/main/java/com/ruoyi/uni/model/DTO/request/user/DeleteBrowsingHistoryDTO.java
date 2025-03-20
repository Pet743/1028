package com.ruoyi.uni.model.DTO.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("删除浏览记录请求")
public class DeleteBrowsingHistoryDTO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long productId;
}

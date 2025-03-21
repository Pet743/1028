package com.ruoyi.uni.model.DTO.request.user.browsing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("浏览记录查询参数")
public class BrowsingHistoryQueryDTO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "查询天数范围（过去几天内的记录，0表示查询全部）")
    private Integer days;

    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页记录数", required = true)
    private Integer pageSize = 10;
}
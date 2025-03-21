package com.ruoyi.uni.model.DTO.respone.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("轮播图DTO")
public class CarouselDTO {

    @ApiModelProperty("内容ID")
    private Long contentId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("图片URL")
    private String imageUrl;

    @ApiModelProperty("链接URL")
    private String linkUrl;

    @ApiModelProperty("排序号")
    private Integer sortOrder;
}
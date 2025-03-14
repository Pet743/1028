package com.ruoyi.uni.model.DTO.request.product;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("商品查询请求DTO")
public class ProductQueryDTO {

    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("每页大小")
    private Integer pageSize = 10;

    @ApiModelProperty("商品分类")
    private String category;

    @ApiModelProperty("商品状态(0:上架 1:下架)")
    private String productStatus;

    @ApiModelProperty("发布人ID")
    private Long publisherId;

    @ApiModelProperty("商品标题关键词")
    private String keyword;
}
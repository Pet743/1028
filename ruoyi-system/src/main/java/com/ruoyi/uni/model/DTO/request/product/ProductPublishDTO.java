package com.ruoyi.uni.model.DTO.request.product;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("商品发布请求DTO")
public class ProductPublishDTO {

    @ApiModelProperty("商品标题")
    private String productTitle;

    @ApiModelProperty("商品图片列表")
    private List<String> productImages;

    @ApiModelProperty("商品描述")
    private String productDescription;

    @ApiModelProperty("商品价格")
    private String productPrice;

    @ApiModelProperty("商品类别")
    private String productCategory;

    @ApiModelProperty("发货方式")
    private String shippingMethod;

    @ApiModelProperty("发布人ID")
    private Long publisherId;
}
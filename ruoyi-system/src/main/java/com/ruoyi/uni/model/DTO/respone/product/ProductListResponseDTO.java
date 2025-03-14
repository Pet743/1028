package com.ruoyi.uni.model.DTO.respone.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("商品列表响应DTO")
public class ProductListResponseDTO {

    @ApiModelProperty("商品ID")
    private Long productId;

    @ApiModelProperty("商品标题")
    private String productTitle;

    @ApiModelProperty("商品封面图片")
    private String productCoverImg;

    @ApiModelProperty("商品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("销售数量")
    private Long salesCount;

    @ApiModelProperty("商品评分")
    private BigDecimal productRating;

    @ApiModelProperty("店铺名称")
    private String shopName;

    @ApiModelProperty("发布人ID")
    private Long publisherId;

    @ApiModelProperty("发布人姓名")
    private String publisherName;

    @ApiModelProperty("商品分类")
    private String productCategory;

    @ApiModelProperty("分类描述")
    private String categoryDesc;

    @ApiModelProperty("发货方式")
    private String shippingMethod;

    @ApiModelProperty("发货方式描述")
    private String shippingMethodDesc;

    @ApiModelProperty("商品状态")
    private String productStatus;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
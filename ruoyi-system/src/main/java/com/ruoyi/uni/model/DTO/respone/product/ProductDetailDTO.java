package com.ruoyi.uni.model.DTO.respone.product;


import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductDetailDTO extends BaseEntity {
    /** 商品ID */
    private Long productId;

    /** 商品标题 */
    @ApiModelProperty(name = "商品标题")
    private String productTitle;

    /** 商品类别 */
    @ApiModelProperty(name = "商品类别")
    private String productCategory;

    /** 商品封面图片路径 */
    @ApiModelProperty(name = "商品封面图片路径")
    private String productCoverImg;

    /** 商品详情图片（JSON格式） */
    @ApiModelProperty(name = "商品详情图片")
    private String productDetailImgs;

    /** 商品描述 */
    @ApiModelProperty(name = "商品描述")
    private String productDescription;

    /** 商品价格 */
    @ApiModelProperty(name = "商品价格")
    private BigDecimal productPrice;

    @ApiModelProperty(name = "商品图片详情")
    private List<String> detailImgList;

    /** 商品发货方式 */
    @ApiModelProperty(name = "商品发货方式")
    private String shippingMethod;

    /** 商品销售数量 */
    @ApiModelProperty(name = "商品销售数量")
    private Long salesCount;

    /** 商品状态（0已上架 1已下架） */
    @ApiModelProperty(name = "商品状态")
    private String productStatus;

    /** 发布人ID */
    @ApiModelProperty(name = "发布人ID")
    private Long publisherId;

    /** 发布人姓名 */
    @ApiModelProperty(name = "发布人姓名")
    private String publisherName;

    /** 发布人手机号 */
    @ApiModelProperty(name = "发布人手机号")
    private String publisherPhone;

    @ApiModelProperty(name = "发布人头像")
    private String publisherImg;


    @ApiModelProperty(name = "是否关注这个用户")
    private Boolean isStar;

    @ApiModelProperty(name = "是否收藏这个商品")
    private Boolean isShopStar;

    /** 商品星级 */
    @ApiModelProperty(name = "商品星级")
    private BigDecimal productRating;

    /** 店铺名称 */
    @ApiModelProperty(name = "店铺名称")
    private String shopName;

    /** 状态（0正常 1停用） */
    @ApiModelProperty(name = "状态")
    private String status;
}

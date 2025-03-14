package com.ruoyi.alse.domain;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品对象 alse_product
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
public class AlseProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long productId;

    /** 商品标题 */
    @Excel(name = "商品标题")
    private String productTitle;

    /** 商品类别 */
    @Excel(name = "商品类别")
    private String productCategory;

    /** 商品封面图片路径 */
    @Excel(name = "商品封面图片路径")
    private String productCoverImg;

    /** 商品详情图片（JSON格式） */
    @Excel(name = "商品详情图片", readConverterExp = "J=SON格式")
    private String productDetailImgs;

    /** 商品描述 */
    @Excel(name = "商品描述")
    private String productDescription;

    /** 商品价格 */
    @Excel(name = "商品价格")
    private BigDecimal productPrice;

    /** 商品发货方式 */
    @Excel(name = "商品发货方式")
    private String shippingMethod;

    /** 商品销售数量 */
    @Excel(name = "商品销售数量")
    private Long salesCount;

    /** 商品状态（0已上架 1已下架） */
    @Excel(name = "商品状态", readConverterExp = "0=已上架,1=已下架")
    private String productStatus;

    /** 发布人ID */
    @Excel(name = "发布人ID")
    private Long publisherId;

    /** 发布人姓名 */
    @Excel(name = "发布人姓名")
    private String publisherName;

    /** 发布人手机号 */
    @Excel(name = "发布人手机号")
    private String publisherPhone;

    /** 商品星级 */
    @Excel(name = "商品星级")
    private BigDecimal productRating;

    /** 店铺名称 */
    @Excel(name = "店铺名称")
    private String shopName;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    // 在AlseProduct类中添加这个字段用于存储解析后的详情图片列表
    private List<String> detailImgList;

    // 对应的getter和setter方法
    public List<String> getDetailImgList() {
        return detailImgList;
    }

    public void setDetailImgList(List<String> detailImgList) {
        this.detailImgList = detailImgList;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setProductTitle(String productTitle) 
    {
        this.productTitle = productTitle;
    }

    public String getProductTitle() 
    {
        return productTitle;
    }

    public void setProductCategory(String productCategory) 
    {
        this.productCategory = productCategory;
    }

    public String getProductCategory() 
    {
        return productCategory;
    }

    public void setProductCoverImg(String productCoverImg) 
    {
        this.productCoverImg = productCoverImg;
    }

    public String getProductCoverImg() 
    {
        return productCoverImg;
    }

    public void setProductDetailImgs(String productDetailImgs) 
    {
        this.productDetailImgs = productDetailImgs;
    }

    public String getProductDetailImgs() 
    {
        return productDetailImgs;
    }

    public void setProductDescription(String productDescription) 
    {
        this.productDescription = productDescription;
    }

    public String getProductDescription() 
    {
        return productDescription;
    }

    public void setProductPrice(BigDecimal productPrice) 
    {
        this.productPrice = productPrice;
    }

    public BigDecimal getProductPrice() 
    {
        return productPrice;
    }

    public void setShippingMethod(String shippingMethod) 
    {
        this.shippingMethod = shippingMethod;
    }

    public String getShippingMethod() 
    {
        return shippingMethod;
    }

    public void setSalesCount(Long salesCount) 
    {
        this.salesCount = salesCount;
    }

    public Long getSalesCount() 
    {
        return salesCount;
    }

    public void setProductStatus(String productStatus) 
    {
        this.productStatus = productStatus;
    }

    public String getProductStatus() 
    {
        return productStatus;
    }

    public void setPublisherId(Long publisherId) 
    {
        this.publisherId = publisherId;
    }

    public Long getPublisherId() 
    {
        return publisherId;
    }

    public void setPublisherName(String publisherName) 
    {
        this.publisherName = publisherName;
    }

    public String getPublisherName() 
    {
        return publisherName;
    }

    public void setPublisherPhone(String publisherPhone) 
    {
        this.publisherPhone = publisherPhone;
    }

    public String getPublisherPhone() 
    {
        return publisherPhone;
    }

    public void setProductRating(BigDecimal productRating) 
    {
        this.productRating = productRating;
    }

    public BigDecimal getProductRating() 
    {
        return productRating;
    }

    public void setShopName(String shopName) 
    {
        this.shopName = shopName;
    }

    public String getShopName() 
    {
        return shopName;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("productId", getProductId())
            .append("productTitle", getProductTitle())
            .append("productCategory", getProductCategory())
            .append("productCoverImg", getProductCoverImg())
            .append("productDetailImgs", getProductDetailImgs())
            .append("productDescription", getProductDescription())
            .append("productPrice", getProductPrice())
            .append("shippingMethod", getShippingMethod())
            .append("salesCount", getSalesCount())
            .append("productStatus", getProductStatus())
            .append("publisherId", getPublisherId())
            .append("publisherName", getPublisherName())
            .append("publisherPhone", getPublisherPhone())
            .append("productRating", getProductRating())
            .append("shopName", getShopName())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

package com.ruoyi.alse.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品订单对象 alse_order
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
public class AlseOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long productId;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String productName;

    /** 商品图片URL */
    @Excel(name = "商品图片URL")
    private String productImageUrl;

    /** 商品单价 */
    @Excel(name = "商品单价")
    private BigDecimal productPrice;

    /** 交易数量 */
    @Excel(name = "交易数量")
    private Long quantity;

    /** 交易总金额 */
    @Excel(name = "交易总金额")
    private BigDecimal totalAmount;

    /** 付款方式(1:支付宝 2:微信 3:银行卡) */
    @Excel(name = "付款方式(1:支付宝 2:微信 3:银行卡)")
    private Integer paymentMethod;

    /** 付款账号信息 */
    @Excel(name = "付款账号信息")
    private String paymentAccount;

    /** 收款方式(1:支付宝 2:微信 3:银行卡) */
    @Excel(name = "收款方式(1:支付宝 2:微信 3:银行卡)")
    private Integer receivingMethod;

    /** 收款账号信息 */
    @Excel(name = "收款账号信息")
    private String receivingAccount;

    /** 买家ID */
    @Excel(name = "买家ID")
    private Long buyerId;

    /** 买家电话 */
    @Excel(name = "买家电话")
    private String buyerPhone;

    /** 买家姓名 */
    @Excel(name = "买家姓名")
    private String buyerName;

    /** 卖家ID */
    @Excel(name = "卖家ID")
    private Long sellerId;

    /** 卖家电话 */
    @Excel(name = "卖家电话")
    private String sellerPhone;

    /** 卖家姓名 */
    @Excel(name = "卖家姓名")
    private String sellerName;

    /** 订单状态(1:待付款 2:待发货 3:待收货 4:待评价 5:退款中 6:已关闭) */
    @Excel(name = "订单状态(1:待付款 2:待发货 3:待收货 4:待评价 5:退款中 6:已关闭)")
    private Integer orderStatus;

    /** 店铺ID */
    @Excel(name = "店铺ID")
    private Long shopId;

    /** 店铺名称 */
    @Excel(name = "店铺名称")
    private String shopName;

    /** 关联钱包交易ID */
    @Excel(name = "关联钱包交易ID")
    private Long walletTransactionId;

    /** 收货地址ID */
    @Excel(name = "收货地址ID")
    private Long shippingAddressId;

    /** 收货地址详情 */
    @Excel(name = "收货地址详情")
    private String shippingAddress;

    /** 物流公司 */
    @Excel(name = "物流公司")
    private String logisticsCompany;

    /** 物流单号 */
    @Excel(name = "物流单号")
    private String logisticsNo;

    /** 付款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "付款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date paymentTime;

    /** 发货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date shippingTime;

    /** 收货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "收货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date receivingTime;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setOrderNo(String orderNo) 
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo() 
    {
        return orderNo;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }

    public void setProductImageUrl(String productImageUrl) 
    {
        this.productImageUrl = productImageUrl;
    }

    public String getProductImageUrl() 
    {
        return productImageUrl;
    }

    public void setProductPrice(BigDecimal productPrice) 
    {
        this.productPrice = productPrice;
    }

    public BigDecimal getProductPrice() 
    {
        return productPrice;
    }

    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setPaymentMethod(Integer paymentMethod) 
    {
        this.paymentMethod = paymentMethod;
    }

    public Integer getPaymentMethod() 
    {
        return paymentMethod;
    }

    public void setPaymentAccount(String paymentAccount) 
    {
        this.paymentAccount = paymentAccount;
    }

    public String getPaymentAccount() 
    {
        return paymentAccount;
    }

    public void setReceivingMethod(Integer receivingMethod) 
    {
        this.receivingMethod = receivingMethod;
    }

    public Integer getReceivingMethod() 
    {
        return receivingMethod;
    }

    public void setReceivingAccount(String receivingAccount) 
    {
        this.receivingAccount = receivingAccount;
    }

    public String getReceivingAccount() 
    {
        return receivingAccount;
    }

    public void setBuyerId(Long buyerId) 
    {
        this.buyerId = buyerId;
    }

    public Long getBuyerId() 
    {
        return buyerId;
    }

    public void setBuyerPhone(String buyerPhone) 
    {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerPhone() 
    {
        return buyerPhone;
    }

    public void setBuyerName(String buyerName) 
    {
        this.buyerName = buyerName;
    }

    public String getBuyerName() 
    {
        return buyerName;
    }

    public void setSellerId(Long sellerId) 
    {
        this.sellerId = sellerId;
    }

    public Long getSellerId() 
    {
        return sellerId;
    }

    public void setSellerPhone(String sellerPhone) 
    {
        this.sellerPhone = sellerPhone;
    }

    public String getSellerPhone() 
    {
        return sellerPhone;
    }

    public void setSellerName(String sellerName) 
    {
        this.sellerName = sellerName;
    }

    public String getSellerName() 
    {
        return sellerName;
    }

    public void setOrderStatus(Integer orderStatus) 
    {
        this.orderStatus = orderStatus;
    }

    public Integer getOrderStatus() 
    {
        return orderStatus;
    }

    public void setShopId(Long shopId) 
    {
        this.shopId = shopId;
    }

    public Long getShopId() 
    {
        return shopId;
    }

    public void setShopName(String shopName) 
    {
        this.shopName = shopName;
    }

    public String getShopName() 
    {
        return shopName;
    }

    public void setWalletTransactionId(Long walletTransactionId) 
    {
        this.walletTransactionId = walletTransactionId;
    }

    public Long getWalletTransactionId() 
    {
        return walletTransactionId;
    }

    public void setShippingAddressId(Long shippingAddressId) 
    {
        this.shippingAddressId = shippingAddressId;
    }

    public Long getShippingAddressId() 
    {
        return shippingAddressId;
    }

    public void setShippingAddress(String shippingAddress) 
    {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingAddress() 
    {
        return shippingAddress;
    }

    public void setLogisticsCompany(String logisticsCompany) 
    {
        this.logisticsCompany = logisticsCompany;
    }

    public String getLogisticsCompany() 
    {
        return logisticsCompany;
    }

    public void setLogisticsNo(String logisticsNo) 
    {
        this.logisticsNo = logisticsNo;
    }

    public String getLogisticsNo() 
    {
        return logisticsNo;
    }

    public void setPaymentTime(Date paymentTime) 
    {
        this.paymentTime = paymentTime;
    }

    public Date getPaymentTime() 
    {
        return paymentTime;
    }

    public void setShippingTime(Date shippingTime) 
    {
        this.shippingTime = shippingTime;
    }

    public Date getShippingTime() 
    {
        return shippingTime;
    }

    public void setReceivingTime(Date receivingTime) 
    {
        this.receivingTime = receivingTime;
    }

    public Date getReceivingTime() 
    {
        return receivingTime;
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
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("productImageUrl", getProductImageUrl())
            .append("productPrice", getProductPrice())
            .append("quantity", getQuantity())
            .append("totalAmount", getTotalAmount())
            .append("paymentMethod", getPaymentMethod())
            .append("paymentAccount", getPaymentAccount())
            .append("receivingMethod", getReceivingMethod())
            .append("receivingAccount", getReceivingAccount())
            .append("buyerId", getBuyerId())
            .append("buyerPhone", getBuyerPhone())
            .append("buyerName", getBuyerName())
            .append("sellerId", getSellerId())
            .append("sellerPhone", getSellerPhone())
            .append("sellerName", getSellerName())
            .append("orderStatus", getOrderStatus())
            .append("shopId", getShopId())
            .append("shopName", getShopName())
            .append("walletTransactionId", getWalletTransactionId())
            .append("shippingAddressId", getShippingAddressId())
            .append("shippingAddress", getShippingAddress())
            .append("logisticsCompany", getLogisticsCompany())
            .append("logisticsNo", getLogisticsNo())
            .append("paymentTime", getPaymentTime())
            .append("shippingTime", getShippingTime())
            .append("receivingTime", getReceivingTime())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

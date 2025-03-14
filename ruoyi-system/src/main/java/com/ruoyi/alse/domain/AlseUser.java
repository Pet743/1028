package com.ruoyi.alse.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户对象 alse_user
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
public class AlseUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String username;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickname;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    /** 身份证号码 */
    @Excel(name = "身份证号码")
    private String idCardNo;

    /** 身份证正面图片路径 */
    @Excel(name = "身份证正面图片路径")
    private String idCardFrontImg;

    /** 身份证反面图片路径 */
    @Excel(name = "身份证反面图片路径")
    private String idCardBackImg;

    /** 用户头像路径 */
    @Excel(name = "用户头像路径")
    private String avatar;

    /** 收款方式(1:支付宝 2:微信 3:银行卡) */
    @Excel(name = "收款方式(1:支付宝 2:微信 3:银行卡)")
    private Integer paymentMethod;

    /** 收款账号 */
    @Excel(name = "收款账号")
    private String paymentAccount;

    /** 总收入 */
    @Excel(name = "总收入")
    private BigDecimal totalIncome;

    /** 总支出 */
    @Excel(name = "总支出")
    private BigDecimal totalExpense;

    /** 钱包余额 */
    @Excel(name = "钱包余额")
    private BigDecimal walletBalance;

    /** 积分 */
    @Excel(name = "积分")
    private Long points;

    /** 红包数量 */
    @Excel(name = "红包数量")
    private Long redPackets;

    /** 收藏的商品（JSON格式） */
    @Excel(name = "收藏的商品", readConverterExp = "J=SON格式")
    private String favoriteProducts;

    /** 浏览记录（JSON格式） */
    @Excel(name = "浏览记录", readConverterExp = "J=SON格式")
    private String browsingHistory;

    /** 关注的用户ID列表（JSON格式） */
    @Excel(name = "关注的用户ID列表", readConverterExp = "J=SON格式")
    private String followedUserIds;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }

    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }

    public void setNickname(String nickname) 
    {
        this.nickname = nickname;
    }

    public String getNickname() 
    {
        return nickname;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }

    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }

    public void setRealName(String realName) 
    {
        this.realName = realName;
    }

    public String getRealName() 
    {
        return realName;
    }

    public void setIdCardNo(String idCardNo) 
    {
        this.idCardNo = idCardNo;
    }

    public String getIdCardNo() 
    {
        return idCardNo;
    }

    public void setIdCardFrontImg(String idCardFrontImg) 
    {
        this.idCardFrontImg = idCardFrontImg;
    }

    public String getIdCardFrontImg() 
    {
        return idCardFrontImg;
    }

    public void setIdCardBackImg(String idCardBackImg) 
    {
        this.idCardBackImg = idCardBackImg;
    }

    public String getIdCardBackImg() 
    {
        return idCardBackImg;
    }

    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
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

    public void setTotalIncome(BigDecimal totalIncome) 
    {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalIncome() 
    {
        return totalIncome;
    }

    public void setTotalExpense(BigDecimal totalExpense) 
    {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getTotalExpense() 
    {
        return totalExpense;
    }

    public void setWalletBalance(BigDecimal walletBalance) 
    {
        this.walletBalance = walletBalance;
    }

    public BigDecimal getWalletBalance() 
    {
        return walletBalance;
    }

    public void setPoints(Long points) 
    {
        this.points = points;
    }

    public Long getPoints() 
    {
        return points;
    }

    public void setRedPackets(Long redPackets) 
    {
        this.redPackets = redPackets;
    }

    public Long getRedPackets() 
    {
        return redPackets;
    }

    public void setFavoriteProducts(String favoriteProducts) 
    {
        this.favoriteProducts = favoriteProducts;
    }

    public String getFavoriteProducts() 
    {
        return favoriteProducts;
    }

    public void setBrowsingHistory(String browsingHistory) 
    {
        this.browsingHistory = browsingHistory;
    }

    public String getBrowsingHistory() 
    {
        return browsingHistory;
    }

    public void setFollowedUserIds(String followedUserIds) 
    {
        this.followedUserIds = followedUserIds;
    }

    public String getFollowedUserIds() 
    {
        return followedUserIds;
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
            .append("userId", getUserId())
            .append("username", getUsername())
            .append("password", getPassword())
            .append("nickname", getNickname())
            .append("phone", getPhone())
            .append("email", getEmail())
            .append("realName", getRealName())
            .append("idCardNo", getIdCardNo())
            .append("idCardFrontImg", getIdCardFrontImg())
            .append("idCardBackImg", getIdCardBackImg())
            .append("avatar", getAvatar())
            .append("paymentMethod", getPaymentMethod())
            .append("paymentAccount", getPaymentAccount())
            .append("totalIncome", getTotalIncome())
            .append("totalExpense", getTotalExpense())
            .append("walletBalance", getWalletBalance())
            .append("points", getPoints())
            .append("redPackets", getRedPackets())
            .append("favoriteProducts", getFavoriteProducts())
            .append("browsingHistory", getBrowsingHistory())
            .append("followedUserIds", getFollowedUserIds())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

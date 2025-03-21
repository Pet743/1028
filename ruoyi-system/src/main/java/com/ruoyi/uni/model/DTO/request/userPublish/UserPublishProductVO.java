package com.ruoyi.uni.model.DTO.request.userPublish;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserPublishProductVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productCoverImg;
    private String productTitle;
    private Date createTime;
    private String productStatus;
    private BigDecimal productPrice;
}
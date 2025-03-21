package com.ruoyi.uni.model.DTO.request.user.browsing;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BrowsingHistoryRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long productId;
    private Date viewTime;
}
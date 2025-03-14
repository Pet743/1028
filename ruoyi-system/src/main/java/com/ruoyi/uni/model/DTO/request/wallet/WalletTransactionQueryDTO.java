package com.ruoyi.uni.model.DTO.request.wallet;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class WalletTransactionQueryDTO {
    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("每页大小")
    private Integer pageSize = 10;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("查询类型(1:今天 2:本周 3:本月 4:自定义)")
    private Integer queryType;

    @ApiModelProperty("自定义开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty("自定义结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty("自定义天数")
    private Integer customDays;

    // getter和setter方法省略
}
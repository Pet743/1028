package com.ruoyi.uni.model.DTO.respone.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("通知公告DTO")
public class NoticeDTO {

    @ApiModelProperty("内容ID")
    private Long contentId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("链接URL")
    private String linkUrl;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
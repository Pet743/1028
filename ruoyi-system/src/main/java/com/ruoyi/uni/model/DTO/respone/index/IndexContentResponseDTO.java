package com.ruoyi.uni.model.DTO.respone.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("首页内容响应DTO")
public class IndexContentResponseDTO {

    @ApiModelProperty("轮播图列表")
    private List<CarouselDTO> carouselList;

    @ApiModelProperty("通知公告列表")
    private List<NoticeDTO> noticeList;
}




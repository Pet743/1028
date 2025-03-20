package com.ruoyi.uni.model.DTO.respone.user;

import com.ruoyi.uni.model.DTO.request.product.BrowsingHistoryItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
@ApiModel("浏览记录响应")
public class BrowsingHistoryResponseDTO {

    @ApiModelProperty(value = "总记录数")
    private Integer total;

    @ApiModelProperty(value = "浏览记录列表")
    private List<BrowsingHistoryItem> records;
}

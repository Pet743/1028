package com.ruoyi.uni.model.DTO.request.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("获取/创建会话请求DTO")
public class GetConversationRequestDTO {

    @ApiModelProperty("目标用户ID")
    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;


    private Long userId;
}
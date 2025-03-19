package com.ruoyi.uni.model.DTO.request.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("标记消息已读请求DTO")
public class ReadMessageRequestDTO {

    @ApiModelProperty("会话ID")
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;
}
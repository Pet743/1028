package com.ruoyi.uni.model.DTO.request.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("获取会话消息列表请求DTO")
public class MessageListRequestDTO {

    @ApiModelProperty("会话ID")
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    @ApiModelProperty("每页记录数")
    private Integer pageSize = 20;

    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("最早消息的ID（用于加载历史消息）")
    private Long beforeMessageId;

    private Long userId;
}
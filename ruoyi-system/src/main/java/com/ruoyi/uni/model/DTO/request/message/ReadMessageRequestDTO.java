package com.ruoyi.uni.model.DTO.request.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("标记消息已读请求DTO")
public class ReadMessageRequestDTO {
    private Long userId;           // 当前用户ID
    private Long conversationId;   // 会话ID
    private Long targetUserId;     // 对方用户ID（新增字段）
}
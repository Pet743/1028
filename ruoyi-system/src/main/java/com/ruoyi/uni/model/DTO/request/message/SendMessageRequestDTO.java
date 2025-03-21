package com.ruoyi.uni.model.DTO.request.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("发送消息请求DTO")
public class SendMessageRequestDTO {

    /**
     * 发送者ID
     */
    @NotNull(message = "发送者ID不能为空")
    private Long userId;

    /**
     * 会话ID (可选，如果为null则自动创建会话)
     */
    private Long conversationId;

    /**
     * 接收者ID
     */
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 内容类型
     */
    @NotNull(message = "内容类型不能为空")
    private Integer contentType;

    /**
     * 媒体URL (可选)
     */
    private String mediaUrl;
}
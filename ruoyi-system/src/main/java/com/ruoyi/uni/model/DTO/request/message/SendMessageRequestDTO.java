package com.ruoyi.uni.model.DTO.request.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("发送消息请求DTO")
public class SendMessageRequestDTO {

    @ApiModelProperty("会话ID")
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    @ApiModelProperty("接收者ID")
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    @ApiModelProperty("消息内容")
    @NotBlank(message = "消息内容不能为空")
    private String content;

    @ApiModelProperty("消息类型：0-文本，1-图片，2-语音，3-视频，4-文件")
    @NotNull(message = "消息类型不能为空")
    private Integer contentType;

    @ApiModelProperty("媒体文件URL（图片/语音/视频/文件消息时必填）")
    private String mediaUrl;
}
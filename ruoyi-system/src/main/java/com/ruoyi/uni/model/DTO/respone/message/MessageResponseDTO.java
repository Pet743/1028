package com.ruoyi.uni.model.DTO.respone.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("消息响应DTO")
public class MessageResponseDTO {

    @ApiModelProperty("消息ID")
    private Long messageId;

    @ApiModelProperty("会话ID")
    private Long conversationId;

    @ApiModelProperty("发送者ID")
    private Long senderId;

    @ApiModelProperty("发送者名称")
    private String senderName;

    @ApiModelProperty("发送者头像")
    private String senderAvatar;

    @ApiModelProperty("接收者ID")
    private Long receiverId;

    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("消息类型：0-文本，1-图片，2-语音，3-视频，4-文件")
    private Integer contentType;

    @ApiModelProperty("媒体文件URL")
    private String mediaUrl;

    @ApiModelProperty("读取状态：0-未读，1-已读")
    private Integer readStatus;

    @ApiModelProperty("是否是自己发送的")
    private Boolean isSelf;

    @ApiModelProperty("发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
}
package com.ruoyi.uni.model.DTO.respone.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("消息列表响应DTO")
public class MessageListResponseDTO {
    @ApiModelProperty("消息ID")
    private Long messageId;

    @ApiModelProperty("发送者ID")
    private Long senderId;

    @ApiModelProperty("发送者名称")
    private String senderName;

    @ApiModelProperty("发送者头像")
    private String senderAvatar;

    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("消息类型：0-文本，1-图片，2-语音，3-视频，4-文件")
    private Integer contentType;

    @ApiModelProperty("媒体文件URL")
    private String mediaUrl;

    @ApiModelProperty("发送时间")
    private Date sendTime;

    @ApiModelProperty("是否是我发送的")
    private Boolean isMine;

    @ApiModelProperty("消息读取状态：0-未读，1-已读")
    private Integer readStatus;
}

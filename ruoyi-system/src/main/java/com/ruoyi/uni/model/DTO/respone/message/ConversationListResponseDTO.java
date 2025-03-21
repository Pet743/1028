package com.ruoyi.uni.model.DTO.respone.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("会话列表响应DTO")
public class ConversationListResponseDTO {

    @ApiModelProperty("会话ID")
    private Long conversationId;

    @ApiModelProperty("会话类型：0-单聊，1-群聊")
    private Integer conversationType;

    @ApiModelProperty("对方用户ID（单聊时）")
    private Long targetUserId;

    @ApiModelProperty("对方用户名称")
    private String targetUserName;

    @ApiModelProperty("对方用户头像")
    private String targetUserAvatar;

    @ApiModelProperty("最后一条消息内容")
    private String lastMessage;

    @ApiModelProperty("最后一条消息时间")
    private Date lastMessageTime;

    @ApiModelProperty("未读消息数")
    private Integer unreadCount;
}

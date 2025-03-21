package com.ruoyi.uni.model.DTO.request.message;

import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel("获取会话消息列表请求DTO")
public class MessageListRequestDTO {

    private Long userId;
}
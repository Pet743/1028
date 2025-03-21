package com.ruoyi.uni.model.DTO.request.user.follow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("关注/取消关注用户请求DTO")
public class FollowUserRequestDTO {

    @ApiModelProperty(value = "当前用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "目标用户ID", required = true)
    private Long targetUserId;

    @ApiModelProperty(value = "是否关注（true-关注，false-取消关注）", required = true)
    private boolean follow;
}
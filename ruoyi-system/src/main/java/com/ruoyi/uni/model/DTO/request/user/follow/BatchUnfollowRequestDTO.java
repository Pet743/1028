package com.ruoyi.uni.model.DTO.request.user.follow;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BatchUnfollowRequestDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotEmpty(message = "取消关注的用户列表不能为空")
    private List<Long> unfollowUserIds;
}

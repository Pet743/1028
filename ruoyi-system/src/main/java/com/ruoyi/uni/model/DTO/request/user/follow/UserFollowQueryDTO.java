package com.ruoyi.uni.model.DTO.request.user.follow;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserFollowQueryDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Integer pageNum = 1;


    private Integer pageSize = 10;
}
package com.ruoyi.uni.model.DTO.request.user.browsing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserIdRequestDTO {
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;
}

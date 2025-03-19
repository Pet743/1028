package com.ruoyi.uni.model.DTO.request.user;

import com.ruoyi.alse.domain.AlseUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户信息编辑请求DTO")
public class UserEditRequestDTO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "用户信息", required = true)
    private AlseUser userInfo;
}
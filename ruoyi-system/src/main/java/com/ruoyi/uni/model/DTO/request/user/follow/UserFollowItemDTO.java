package com.ruoyi.uni.model.DTO.request.user.follow;

import lombok.Data;

@Data
public class UserFollowItemDTO {
    private Long userId;
    private String avatar;
    private String userName;
    private String contactInfo; // 脱敏后的手机号或邮箱
    private Boolean isVerified; // 是否认证
}
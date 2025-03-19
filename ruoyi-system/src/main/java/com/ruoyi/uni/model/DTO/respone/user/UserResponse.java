package com.ruoyi.uni.model.DTO.respone.user;

import lombok.Data;

@Data
public class UserResponse {
    private String token;

    public Long userId;
}

package com.ruoyi.uni.converter;


import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.model.DTO.respone.user.UserInfoResponseDTO;

/**
 * 用户信息转换工具类
 */
public class UserConverter {

    /**
     * 将AlseUser实体转换为UserInfoResponseDTO
     */
    public static UserInfoResponseDTO convertToUserInfoDTO(AlseUser user) {
        if (user == null) {
            return null;
        }

        UserInfoResponseDTO dto = new UserInfoResponseDTO();

        // 基本信息转换
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setRealName(user.getRealName());
        dto.setAvatar(user.getAvatar());
        dto.setPaymentMethod(user.getPaymentMethod());
        dto.setPaymentAccount(user.getPaymentAccount());
        dto.setPaymentMethodIds(user.getPaymentMethodIds());
        dto.setPoints(user.getPoints());

        // 设置认证状态
        if (!StringUtils.isEmpty(user.getIdCardNo())) {
            dto.setVerifyStatus("1");
            dto.setVerifyStatusDesc("审核中");
        } else {
            dto.setVerifyStatus("0");
            dto.setVerifyStatusDesc("用户未认证");
        }

        return dto;
    }
}
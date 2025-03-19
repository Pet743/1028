package com.ruoyi.uni.model.Enum;

import lombok.Getter;

/**
 * 会话类型枚举
 */
@Getter
public enum ConversationTypeEnum {
    SINGLE(0, "单聊"),
    GROUP(1, "群聊");

    private final Integer code;
    private final String desc;

    ConversationTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        for (ConversationTypeEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item.getDesc();
            }
        }
        return null;
    }
}
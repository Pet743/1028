package com.ruoyi.uni.model.Enum;

import lombok.Getter;

/**
 * 消息内容类型枚举
 */
@Getter
public enum MessageContentTypeEnum {
    TEXT(0, "文本"),
    IMAGE(1, "图片"),
    VOICE(2, "语音"),
    VIDEO(3, "视频"),
    FILE(4, "文件");

    private final Integer code;
    private final String desc;

    MessageContentTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        for (MessageContentTypeEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item.getDesc();
            }
        }
        return null;
    }
}
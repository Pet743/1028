package com.ruoyi.uni.model.Enum;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.function.BiFunction;

public enum DateRangeTypeEnum {

    /**
     * 今天
     */
    TODAY(1, (customDays, dates) -> {
        LocalDateTime now = LocalDateTime.now();
        Date start = Date.from(now.with(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(now.with(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        return new Date[] { start, end };
    }),

    /**
     * 本周
     */
    THIS_WEEK(2, (customDays, dates) -> {
        LocalDateTime now = LocalDateTime.now();
        Date start = Date.from(now.with(DayOfWeek.MONDAY).with(LocalTime.MIN)
                .atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(now.with(DayOfWeek.SUNDAY).with(LocalTime.MAX)
                .atZone(ZoneId.systemDefault()).toInstant());
        return new Date[] { start, end };
    }),

    /**
     * 本月
     */
    THIS_MONTH(3, (customDays, dates) -> {
        LocalDateTime now = LocalDateTime.now();
        Date start = Date.from(now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN)
                .atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX)
                .atZone(ZoneId.systemDefault()).toInstant());
        return new Date[] { start, end };
    }),

    /**
     * 自定义时间范围
     */
    CUSTOM(4, (customDays, dates) -> {
        // 优先使用自定义天数
        if (customDays != null && customDays > 0) {
            LocalDateTime now = LocalDateTime.now();
            Date start = Date.from(now.minusDays(customDays).with(LocalTime.MIN)
                    .atZone(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(now.with(LocalTime.MAX)
                    .atZone(ZoneId.systemDefault()).toInstant());
            return new Date[] { start, end };
        }
        // 其次使用自定义日期范围
        else if (dates != null && dates.length >= 2 && dates[0] != null && dates[1] != null) {
            return new Date[] { dates[0], dates[1] };
        }
        // 默认返回今天
        return TODAY.calculateDateRange(null, null);
    });

    private final int value;
    private final BiFunction<Integer, Date[], Date[]> dateRangeCalculator;

    DateRangeTypeEnum(int value, BiFunction<Integer, Date[], Date[]> dateRangeCalculator) {
        this.value = value;
        this.dateRangeCalculator = dateRangeCalculator;
    }

    public int getValue() {
        return value;
    }

    /**
     * 计算日期范围
     *
     * @param customDays 自定义天数
     * @param dates 自定义日期范围，第一个元素为开始日期，第二个元素为结束日期
     * @return 计算后的日期范围，第一个元素为开始日期，第二个元素为结束日期
     */
    public Date[] calculateDateRange(Integer customDays, Date[] dates) {
        return dateRangeCalculator.apply(customDays, dates);
    }

    /**
     * 根据值获取枚举
     *
     * @param value 枚举值
     * @return 枚举实例
     */
    public static DateRangeTypeEnum getByValue(Integer value) {
        if (value == null) {
            return TODAY; // 默认返回今天
        }

        for (DateRangeTypeEnum type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return TODAY; // 默认返回今天
    }
}

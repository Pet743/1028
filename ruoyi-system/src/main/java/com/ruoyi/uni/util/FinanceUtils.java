package com.ruoyi.uni.util;

import com.ruoyi.uni.model.common.CommissionTier;
import com.ruoyi.uni.model.common.TransactionStats;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.util.DoubleRounder;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 金融计算工具类
 * 基于decimal4j和joda-money的高精度财务计算工具
 */
public class FinanceUtils {

    // =============== 常量定义 ===============

    /** 默认货币单位(人民币) */
    public static final CurrencyUnit CNY = CurrencyUnit.of("CNY");

    /** 美元货币单位 */
    public static final CurrencyUnit USD = CurrencyUnit.of("USD");

    /** 默认精度(小数位数) */
    public static final int DEFAULT_SCALE = 2;

    /** 默认舍入模式 */
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    /** 金额为0 */
    public static final BigDecimal ZERO = BigDecimal.ZERO;

    /** BigDecimal 100 */
    public static final BigDecimal HUNDRED = new BigDecimal("100");

    /** 默认最大金额 */
    public static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999.99");

    /** 最小允许金额 */
    public static final BigDecimal MIN_AMOUNT = new BigDecimal("0.01");

    /** 金额精度环境 */
    private static final ScaleMetrics MONEY_METRICS = Scales.getScaleMetrics(DEFAULT_SCALE);

    /** 高精度运算环境 */
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    // =============== 格式化相关 ===============

    /** 金额格式化器(中文格式) */
    private static final MoneyFormatter CNY_FORMATTER = new MoneyFormatterBuilder()
            .appendCurrencySymbolLocalized()
            .appendAmountLocalized()
            .toFormatter(Locale.CHINA);

    /** 金额格式化器(美式格式) */
    private static final MoneyFormatter USD_FORMATTER = new MoneyFormatterBuilder()
            .appendCurrencySymbolLocalized()
            .appendAmountLocalized()
            .toFormatter(Locale.US);

    /** 纯数字格式(两位小数) */
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    /** 金额正则表达式 */
    private static final Pattern MONEY_PATTERN = Pattern.compile("^-?\\d+(\\.\\d{1,2})?$");

    static {
        NUMBER_FORMAT.setMinimumFractionDigits(DEFAULT_SCALE);
        NUMBER_FORMAT.setMaximumFractionDigits(DEFAULT_SCALE);
        NUMBER_FORMAT.setRoundingMode(DEFAULT_ROUNDING);
        NUMBER_FORMAT.setGroupingUsed(false);
    }

    // =============== 类型转换 ===============

    /**
     * 字符串转BigDecimal
     * 自动清理货币符号和千分位
     */
    public static BigDecimal toBigDecimal(String str) {
        if (str == null || str.trim().isEmpty()) {
            return ZERO;
        }
        // 移除货币符号、空格和千分位
        str = str.replaceAll("[¥$,\\s]", "").trim();
        try {
            return normalizeAmount(new BigDecimal(str));
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的金额格式: " + str, e);
        }
    }

    /**
     * BigDecimal转Money对象(人民币)
     */
    public static Money toMoney(BigDecimal amount) {
        if (amount == null) {
            return Money.zero(CNY);
        }
        return Money.of(CNY, amount);
    }

    /**
     * BigDecimal转Money对象(指定币种)
     */
    public static Money toMoney(CurrencyUnit currency, BigDecimal amount) {
        if (amount == null) {
            return Money.zero(currency);
        }
        return Money.of(currency, amount);
    }

    /**
     * Money对象转BigDecimal
     */
    public static BigDecimal toBigDecimal(Money money) {
        if (money == null) {
            return ZERO;
        }
        return money.getAmount();
    }

    /**
     * 规范化金额(两位小数)
     */
    public static BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            return ZERO;
        }
        return amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * 规范化金额(两位小数)，支持传入 String 类型
     */
    public static BigDecimal normalizeAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return ZERO;
        }
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            return normalizeAmount(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的金额格式: " + amountStr, e);
        }
    }

    /**
     * 规范化金额(指定小数位)
     */
    public static BigDecimal normalizeAmount(BigDecimal amount, int scale) {
        if (amount == null) {
            return ZERO;
        }
        return amount.setScale(scale, DEFAULT_ROUNDING);
    }

    /**
     * double精确舍入(使用decimal4j)
     */
    public static double roundDouble(double value, int scale) {
        return DoubleRounder.round(value, scale);
    }

    // =============== 格式化 ===============

    /**
     * 格式化Money为字符串(带货币符号)
     */
    public static String format(Money money) {
        if (money == null) {
            return CNY_FORMATTER.print(Money.zero(CNY));
        }

        if (CNY.equals(money.getCurrencyUnit())) {
            return CNY_FORMATTER.print(money);
        } else if (USD.equals(money.getCurrencyUnit())) {
            return USD_FORMATTER.print(money);
        } else {
            return money.toString();
        }
    }

    /**
     * 格式化BigDecimal为货币字符串(人民币)
     */
    public static String formatCNY(BigDecimal amount) {
        return format(toMoney(amount));
    }

    /**
     * 格式化BigDecimal为货币字符串(美元)
     */
    public static String formatUSD(BigDecimal amount) {
        return format(toMoney(USD, amount));
    }

    /**
     * 格式化为纯数字字符串(两位小数)
     */
    public static String formatNumber(BigDecimal amount) {
        if (amount == null) {
            return NUMBER_FORMAT.format(ZERO);
        }
        return NUMBER_FORMAT.format(amount);
    }

    // =============== 校验 ===============

    /**
     * 检查是否有效金额字符串
     */
    public static boolean isValidMoneyString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        str = str.replaceAll("[¥$,\\s]", "").trim();
        return MONEY_PATTERN.matcher(str).matches();
    }

    /**
     * 检查金额是否有效且在范围内
     */
    public static boolean isValidAmount(BigDecimal amount, BigDecimal min, BigDecimal max) {
        if (amount == null) {
            return false;
        }
        return amount.compareTo(min) >= 0 && amount.compareTo(max) <= 0;
    }

    /**
     * 检查金额是否为正数
     */
    public static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(ZERO) > 0;
    }

    /**
     * 检查金额是否为零
     */
    public static boolean isZero(BigDecimal amount) {
        return amount != null && amount.compareTo(ZERO) == 0;
    }

    /**
     * 检查金额是否为负数
     */
    public static boolean isNegative(BigDecimal amount) {
        return amount != null && amount.compareTo(ZERO) < 0;
    }

    /**
     * 验证并标准化金额(抛出异常)
     */
    public static BigDecimal validateAmount(BigDecimal amount) {
        Assert.notNull(amount, "金额不能为空");
        Assert.isTrue(amount.compareTo(ZERO) >= 0, "金额不能为负数");
        return normalizeAmount(amount);
    }

    /**
     * 验证金额在指定范围内(抛出异常)
     */
    public static BigDecimal validateAmount(BigDecimal amount, BigDecimal min, BigDecimal max) {
        validateAmount(amount);
        Assert.isTrue(amount.compareTo(min) >= 0, "金额不能小于" + formatNumber(min));
        Assert.isTrue(amount.compareTo(max) <= 0, "金额不能大于" + formatNumber(max));
        return normalizeAmount(amount);
    }

    /**
     * 校验数量
     */
    public static boolean isValidQuantity(Integer quantity, Integer minQuantity, Integer maxQuantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }
        if (minQuantity != null && quantity < minQuantity) {
            return false;
        }
        return maxQuantity == null || quantity <= maxQuantity;
    }

    /**
     * 验证数量(抛出异常)
     */
    public static Integer validateQuantity(Integer quantity) {
        Assert.notNull(quantity, "数量不能为空");
        Assert.isTrue(quantity > 0, "数量必须大于0");
        return quantity;
    }

    // =============== 基础运算 ===============

    /**
     * 加法运算
     */
    public static BigDecimal add(BigDecimal... amounts) {
        BigDecimal result = ZERO;
        if (amounts != null) {
            for (BigDecimal amount : amounts) {
                if (amount != null) {
                    result = result.add(amount);
                }
            }
        }
        return normalizeAmount(result);
    }

    /**
     * 减法运算
     */
    public static BigDecimal subtract(BigDecimal minuend, BigDecimal subtrahend) {
        if (minuend == null) minuend = ZERO;
        if (subtrahend == null) subtrahend = ZERO;
        return normalizeAmount(minuend.subtract(subtrahend));
    }

    /**
     * 乘法运算
     */
    public static BigDecimal multiply(BigDecimal amount, BigDecimal multiplier) {
        if (amount == null || multiplier == null) {
            return ZERO;
        }
        return normalizeAmount(amount.multiply(multiplier));
    }

    /**
     * 乘法运算(金额乘以整数)
     */
    public static BigDecimal multiply(BigDecimal amount, int multiplier) {
        if (amount == null) {
            return ZERO;
        }
        return normalizeAmount(amount.multiply(new BigDecimal(multiplier)));
    }

    /**
     * 除法运算
     */
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        if (dividend == null) return ZERO;
        Assert.notNull(divisor, "除数不能为空");
        Assert.isTrue(divisor.compareTo(ZERO) != 0, "除数不能为0");
        return dividend.divide(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * 除法运算(金额除以整数)
     */
    public static BigDecimal divide(BigDecimal dividend, int divisor) {
        if (dividend == null) return ZERO;
        Assert.isTrue(divisor != 0, "除数不能为0");
        return dividend.divide(new BigDecimal(divisor), DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * 取余运算
     */
    public static BigDecimal remainder(BigDecimal dividend, BigDecimal divisor) {
        if (dividend == null) return ZERO;
        Assert.notNull(divisor, "除数不能为空");
        Assert.isTrue(divisor.compareTo(ZERO) != 0, "除数不能为0");
        return normalizeAmount(dividend.remainder(divisor));
    }

    /**
     * 取绝对值
     */
    public static BigDecimal abs(BigDecimal amount) {
        if (amount == null) return ZERO;
        return normalizeAmount(amount.abs());
    }

    /**
     * 取反值
     */
    public static BigDecimal negate(BigDecimal amount) {
        if (amount == null) return ZERO;
        return normalizeAmount(amount.negate());
    }

    /**
     * 计算百分比值
     */
    public static BigDecimal percentage(BigDecimal amount, BigDecimal rate) {
        if (amount == null || rate == null) {
            return ZERO;
        }
        return multiply(amount, divide(rate, HUNDRED));
    }

    // =============== 业务计算 ===============

    /**
     * 计算总金额(单价 x 数量)
     */
    public static BigDecimal calculateTotal(BigDecimal unitPrice, long quantity) {
        if (unitPrice == null || quantity <= 0) {
            return ZERO;
        }
        return multiply(unitPrice, BigDecimal.valueOf(quantity));
    }

    /**
     * 计算单价(总金额 ÷ 数量)
     */
    public static BigDecimal calculateUnitPrice(BigDecimal totalAmount, int quantity) {
        if (totalAmount == null || quantity <= 0) {
            return ZERO;
        }
        return divide(totalAmount, quantity);
    }

    /**
     * 计算比例佣金
     */
    public static BigDecimal calculateCommission(BigDecimal amount, BigDecimal rate) {
        return percentage(amount, rate);
    }

    /**
     * 计算阶梯佣金
     */
    public static BigDecimal calculateTieredCommission(BigDecimal amount, List<CommissionTier> tiers) {
        Assert.notNull(amount, "金额不能为空");
        if (tiers == null || tiers.isEmpty()) {
            return ZERO;
        }

        // 对阶梯按上限排序
        tiers.sort(Comparator.comparing(CommissionTier::getUpperLimit));

        for (CommissionTier tier : tiers) {
            if (amount.compareTo(tier.getUpperLimit()) <= 0) {
                return calculateCommission(amount, tier.getRate());
            }
        }

        // 使用最后一个阶梯的费率
        return calculateCommission(amount, tiers.get(tiers.size() - 1).getRate());
    }

    /**
     * 计算折扣价格
     */
    public static BigDecimal calculateDiscountedPrice(BigDecimal price, BigDecimal discountRate) {
        if (price == null || discountRate == null) {
            return ZERO;
        }
        return subtract(price, percentage(price, discountRate));
    }

    /**
     * 计算含税价格
     */
    public static BigDecimal calculatePriceWithTax(BigDecimal price, BigDecimal taxRate) {
        if (price == null || taxRate == null) {
            return ZERO;
        }
        return add(price, percentage(price, taxRate));
    }

    /**
     * 按比例分配金额
     */
    public static List<BigDecimal> allocateByRatio(BigDecimal totalAmount, List<BigDecimal> ratios) {
        if (totalAmount == null || ratios == null || ratios.isEmpty()) {
            return Collections.emptyList();
        }

        List<BigDecimal> results = new ArrayList<>(ratios.size());
        BigDecimal totalRatio = ratios.stream()
                .filter(Objects::nonNull)
                .reduce(ZERO, BigDecimal::add);

        if (totalRatio.compareTo(ZERO) <= 0) {
            return Collections.nCopies(ratios.size(), ZERO);
        }

        BigDecimal allocated = ZERO;

        // 分配前n-1个金额
        for (int i = 0; i < ratios.size() - 1; i++) {
            BigDecimal ratio = ratios.get(i);
            if (ratio == null || ratio.compareTo(ZERO) <= 0) {
                results.add(ZERO);
                continue;
            }

            BigDecimal amount = multiply(totalAmount, divide(ratio, totalRatio));
            results.add(amount);
            allocated = add(allocated, amount);
        }

        // 最后一个金额通过总额减已分配额计算，避免舍入误差
        results.add(subtract(totalAmount, allocated));

        return results;
    }

    /**
     * 均分金额
     */
    public static List<BigDecimal> allocateEvenly(BigDecimal totalAmount, int parts) {
        if (totalAmount == null || parts <= 0) {
            return Collections.emptyList();
        }

        BigDecimal perPart = divide(totalAmount, parts);
        BigDecimal remainder = remainder(totalAmount, new BigDecimal(parts));

        List<BigDecimal> results = new ArrayList<>(parts);

        for (int i = 0; i < parts; i++) {
            if (i == parts - 1 && remainder.compareTo(ZERO) != 0) {
                // 最后一份加上余数
                results.add(add(perPart, remainder));
            } else {
                results.add(perPart);
            }
        }

        return results;
    }

    /**
     * 计算流水统计
     */
    public static TransactionStats calculateTransactionStats(Collection<BigDecimal> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return new TransactionStats(ZERO, ZERO, ZERO, 0L);
        }

        BigDecimal income = transactions.stream()
                .filter(t -> t != null && t.compareTo(ZERO) > 0)
                .reduce(ZERO, BigDecimal::add);

        BigDecimal expense = transactions.stream()
                .filter(t -> t != null && t.compareTo(ZERO) < 0)
                .reduce(ZERO, BigDecimal::add);

        BigDecimal balance = add(income, expense);
        long count = transactions.size();

        return new TransactionStats(
                normalizeAmount(income),
                normalizeAmount(expense),
                normalizeAmount(balance),
                count
        );
    }

    /**
     * 分组统计金额
     *
     * @param items 需要统计的项目集合
     * @param groupExtractor 分组键提取器
     * @param amountExtractor 金额提取器
     * @return 分组后的金额统计
     */
    public static <T, K> Map<K, BigDecimal> groupSum(
            Collection<T> items,
            Function<T, K> groupExtractor,
            Function<T, BigDecimal> amountExtractor) {

        if (items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }

        return items.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        groupExtractor,
                        Collectors.mapping(
                                amountExtractor,
                                Collectors.reducing(ZERO, (a, b) -> a != null && b != null ? add(a, b) :
                                        a != null ? a : b != null ? b : ZERO)
                        )
                ));
    }

    // =============== 比较操作 ===============

    /**
     * 比较两个金额是否相等
     */
    public static boolean equals(BigDecimal amount1, BigDecimal amount2) {
        return normalizeAmount(amount1).compareTo(normalizeAmount(amount2)) == 0;
    }

    /**
     * 获取两个金额中的较大值
     */
    public static BigDecimal max(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) return normalizeAmount(amount2);
        if (amount2 == null) return normalizeAmount(amount1);
        return amount1.compareTo(amount2) >= 0 ? normalizeAmount(amount1) : normalizeAmount(amount2);
    }

    /**
     * 获取两个金额中的较小值
     */
    public static BigDecimal min(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) return normalizeAmount(amount2);
        if (amount2 == null) return normalizeAmount(amount1);
        return amount1.compareTo(amount2) <= 0 ? normalizeAmount(amount1) : normalizeAmount(amount2);
    }

    /**
     * 检查金额是否在范围内
     */
    public static boolean isInRange(BigDecimal amount, BigDecimal min, BigDecimal max) {
        if (amount == null) return false;
        boolean minOk = min == null || amount.compareTo(min) >= 0;
        boolean maxOk = max == null || amount.compareTo(max) <= 0;
        return minOk && maxOk;
    }

    // =============== Money对象操作 ===============

    /**
     * Money对象加法
     */
    public static Money add(Money money1, Money money2) {
        if (money1 == null) {
            return money2 != null ? money2 : Money.zero(CNY);
        }
        if (money2 == null) {
            return money1;
        }
        return money1.plus(money2);
    }

    /**
     * Money对象减法
     */
    public static Money subtract(Money money1, Money money2) {
        if (money1 == null) {
            return money2 != null ? money2.negated() : Money.zero(CNY);
        }
        if (money2 == null) {
            return money1;
        }
        return money1.minus(money2);
    }

    /**
     * Money对象乘法
     */
    public static Money multiply(Money money, BigDecimal multiplier) {
        if (money == null) {
            return Money.zero(CNY);
        }
        if (multiplier == null) {
            return money;
        }
        // 修复：使用接受BigDecimal参数的重载方法，同时提供舍入模式
        return money.multipliedBy(multiplier, DEFAULT_ROUNDING);
    }

    /**
     * Money对象乘法(整数倍)
     */
    public static Money multiplyBy(Money money, long multiplier) {
        if (money == null) {
            return Money.zero(CNY);
        }
        return money.multipliedBy(multiplier);
    }

    /**
     * Money对象除法
     */
    public static Money divide(Money money, BigDecimal divisor) {
        if (money == null) {
            return Money.zero(CNY);
        }
        Assert.notNull(divisor, "除数不能为空");
        Assert.isTrue(divisor.compareTo(ZERO) != 0, "除数不能为0");
        return money.dividedBy(divisor, DEFAULT_ROUNDING);
    }

}



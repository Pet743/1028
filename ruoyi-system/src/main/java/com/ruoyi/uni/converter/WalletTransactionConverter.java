package com.ruoyi.uni.converter;

import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.domain.AlseWalletTransaction;
import com.ruoyi.uni.model.DTO.respone.wallet.WalletBalanceResponseDTO;
import com.ruoyi.uni.model.DTO.respone.wallet.WalletTransactionResponseDTO;
import com.ruoyi.uni.model.Enum.PaymentMethodEnum;
import com.ruoyi.uni.model.Enum.TransactionTypeEnum;
import com.ruoyi.uni.util.FinanceUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WalletTransactionConverter {

    /**
     * 将交易实体转换为响应DTO
     */
    public WalletTransactionResponseDTO toResponseDTO(AlseWalletTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        WalletTransactionResponseDTO dto = new WalletTransactionResponseDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setUserId(transaction.getUserId());
        dto.setUsername(transaction.getUsername());

        // 格式化金额数据为字符串
        dto.setTransactionAmount(FinanceUtils.formatNumber(transaction.getTransactionAmount()));
        dto.setIncomeAmount(FinanceUtils.formatNumber(transaction.getIncomeAmount()));
        dto.setExpenseAmount(FinanceUtils.formatNumber(transaction.getExpenseAmount()));

        dto.setPaymentMethod(transaction.getPaymentMethod());

        // 使用枚举获取描述
        PaymentMethodEnum paymentMethod = PaymentMethodEnum.getByCode(transaction.getPaymentMethod());
        dto.setPaymentMethodDesc(paymentMethod.getDesc());

        dto.setTransactionType(transaction.getTransactionType());

        // 使用枚举获取描述
        TransactionTypeEnum transactionType = TransactionTypeEnum.getByCode(transaction.getTransactionType());
        dto.setTransactionTypeDesc(transactionType.getDesc());

        dto.setTransactionTime(transaction.getTransactionTime());
        dto.setCreateTime(transaction.getCreateTime());
        dto.setRemark(transaction.getRemark());

        return dto;
    }

    /**
     * 将交易列表转换为响应DTO列表
     */
    public List<WalletTransactionResponseDTO> toResponseDTOList(List<AlseWalletTransaction> transactionList) {
        if (transactionList == null || transactionList.isEmpty()) {
            return new ArrayList<>();
        }

        return transactionList.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 转换钱包余额数据
     */
    public WalletBalanceResponseDTO toBalanceResponseDTO(AlseUser user) {
        if (user == null) {
            return null;
        }

        WalletBalanceResponseDTO dto = new WalletBalanceResponseDTO();

        // 获取钱包余额
        BigDecimal walletBalance = user.getWalletBalance() != null ?
                user.getWalletBalance() : BigDecimal.ZERO;
        dto.setWalletBalance(FinanceUtils.formatNumber(walletBalance));

        // 获取总收入
        BigDecimal totalIncome = user.getTotalIncome() != null ?
                user.getTotalIncome() : BigDecimal.ZERO;
        dto.setTotalIncome(FinanceUtils.formatNumber(totalIncome));

        // 获取总支出
        BigDecimal totalExpense = user.getTotalExpense() != null ?
                user.getTotalExpense() : BigDecimal.ZERO;
        dto.setTotalExpense(FinanceUtils.formatNumber(totalExpense));

        // 检查钱包余额是否等于总收入减总支出
        BigDecimal calculatedBalance = FinanceUtils.subtract(totalIncome, totalExpense);
        boolean balanceCorrect = FinanceUtils.equals(walletBalance, calculatedBalance);
        dto.setBalanceCorrect(balanceCorrect);

        return dto;
    }
}
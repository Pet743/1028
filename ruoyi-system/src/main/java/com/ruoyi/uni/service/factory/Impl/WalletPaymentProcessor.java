package com.ruoyi.uni.service.factory.Impl;

import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.domain.AlseWalletTransaction;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.alse.service.IAlseWalletTransactionService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.uni.model.DTO.respone.order.PaymentResultDTO;
import com.ruoyi.uni.model.Enum.OrderStatusEnum;
import com.ruoyi.uni.model.Enum.PaymentMethodEnum;
import com.ruoyi.uni.service.PaymentProcessor;
import com.ruoyi.uni.util.FinanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class WalletPaymentProcessor implements PaymentProcessor {

    @Autowired
    private IAlseWalletTransactionService walletTransactionService;

    @Autowired
    private IAlseUserService userService;

    @Override
    public Integer getSupportedPaymentMethod() {
        return PaymentMethodEnum.WALLET.getCode();
    }

    @Override
    public PaymentResultDTO processPayment(AlseOrder order, AlseProduct product, AlseUser buyer) {
        log.info("处理钱包支付，订单号: {}", order.getOrderNo());

        // 检查余额是否充足
        if (buyer.getWalletBalance().compareTo(order.getTotalAmount()) < 0) {
            throw new ServiceException("钱包余额不足，请充值后再支付");
        }

        // 创建钱包交易记录
        AlseWalletTransaction transaction = new AlseWalletTransaction();
        transaction.setUserId(buyer.getUserId());
        transaction.setUsername(buyer.getUsername());
        transaction.setTransactionAmount(order.getTotalAmount());
        transaction.setExpenseAmount(order.getTotalAmount());
        transaction.setIncomeAmount(BigDecimal.ZERO);
        transaction.setPaymentMethod(PaymentMethodEnum.WALLET.getCode());
        transaction.setTransactionType(1); // 购买商品
        transaction.setTransactionTime(DateUtils.getNowDate());
        transaction.setStatus("0");
        transaction.setCreateBy(buyer.getUsername());
        transaction.setCreateTime(DateUtils.getNowDate());
        transaction.setUpdateBy(buyer.getUsername());
        transaction.setUpdateTime(DateUtils.getNowDate());
        transaction.setRemark("订单支付：" + order.getOrderNo());

        // 插入交易记录
        walletTransactionService.insertAlseWalletTransaction(transaction);

        // 更新订单交易ID和支付时间
        order.setWalletTransactionId(transaction.getTransactionId());
        order.setPaymentTime(DateUtils.getNowDate());
        order.setOrderStatus(OrderStatusEnum.PENDING_SHIPMENT.getCode());

        // 更新用户钱包余额（扣除支付金额）
        buyer.setWalletBalance(FinanceUtils.subtract(buyer.getWalletBalance(), order.getTotalAmount()));
        buyer.setTotalExpense(FinanceUtils.add(buyer.getTotalExpense(), order.getTotalAmount()));
        buyer.setUpdateTime(DateUtils.getNowDate());
        buyer.setUpdateBy(buyer.getUsername());

        int result = userService.updateAlseUser(buyer);
        if (result <= 0) {
            throw new ServiceException("更新钱包余额失败，请稍后重试");
        }

        // 构建支付结果
        PaymentResultDTO resultDTO = new PaymentResultDTO();
        resultDTO.setOrderId(order.getOrderId());
        resultDTO.setOrderNo(order.getOrderNo());
        resultDTO.setPaymentMethod(PaymentMethodEnum.WALLET.getCode());
        resultDTO.setTotalAmount(order.getTotalAmount());
        resultDTO.setPaymentStatus(2); // 支付成功
        resultDTO.setRedirectType(5); // 无需跳转

        return resultDTO;
    }

    @Override
    public boolean handlePaymentCallback(Map<String, String> params) {
        // 钱包支付无需回调处理
        return true;
    }
}

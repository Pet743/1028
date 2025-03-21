package com.ruoyi.alse.service.impl;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlseWalletTransactionMapper;
import com.ruoyi.alse.domain.AlseWalletTransaction;
import com.ruoyi.alse.service.IAlseWalletTransactionService;

/**
 * 钱包交易流水Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
@Service
public class AlseWalletTransactionServiceImpl implements IAlseWalletTransactionService 
{
    @Autowired
    private AlseWalletTransactionMapper alseWalletTransactionMapper;

    /**
     * 查询钱包交易流水
     * 
     * @param transactionId 钱包交易流水主键
     * @return 钱包交易流水
     */
    @Override
    public AlseWalletTransaction selectAlseWalletTransactionByTransactionId(Long transactionId)
    {
        return alseWalletTransactionMapper.selectAlseWalletTransactionByTransactionId(transactionId);
    }

    /**
     * 查询钱包交易流水列表
     * 
     * @param alseWalletTransaction 钱包交易流水
     * @return 钱包交易流水
     */
    @Override
    public List<AlseWalletTransaction> selectAlseWalletTransactionList(AlseWalletTransaction alseWalletTransaction)
    {
        return alseWalletTransactionMapper.selectAlseWalletTransactionList(alseWalletTransaction);
    }

    /**
     * 根据条件查询钱包交易记录（支持收入/支出筛选）
     *
     * @param params 查询条件
     * @return 交易记录列表
     */
    @Override
    public List<AlseWalletTransaction> selectWalletTransactionByCategory(Map<String, Object> params) {
        return alseWalletTransactionMapper.selectWalletTransactionByCategory(params);
    }

    /**
     * 新增钱包交易流水
     * 
     * @param alseWalletTransaction 钱包交易流水
     * @return 结果
     */
    @Override
    public int insertAlseWalletTransaction(AlseWalletTransaction alseWalletTransaction)
    {
        alseWalletTransaction.setCreateTime(DateUtils.getNowDate());
        return alseWalletTransactionMapper.insertAlseWalletTransaction(alseWalletTransaction);
    }

    /**
     * 修改钱包交易流水
     * 
     * @param alseWalletTransaction 钱包交易流水
     * @return 结果
     */
    @Override
    public int updateAlseWalletTransaction(AlseWalletTransaction alseWalletTransaction)
    {
        alseWalletTransaction.setUpdateTime(DateUtils.getNowDate());
        return alseWalletTransactionMapper.updateAlseWalletTransaction(alseWalletTransaction);
    }

    /**
     * 批量删除钱包交易流水
     * 
     * @param transactionIds 需要删除的钱包交易流水主键
     * @return 结果
     */
    @Override
    public int deleteAlseWalletTransactionByTransactionIds(Long[] transactionIds)
    {
        return alseWalletTransactionMapper.deleteAlseWalletTransactionByTransactionIds(transactionIds);
    }

    /**
     * 删除钱包交易流水信息
     * 
     * @param transactionId 钱包交易流水主键
     * @return 结果
     */
    @Override
    public int deleteAlseWalletTransactionByTransactionId(Long transactionId)
    {
        return alseWalletTransactionMapper.deleteAlseWalletTransactionByTransactionId(transactionId);
    }
}

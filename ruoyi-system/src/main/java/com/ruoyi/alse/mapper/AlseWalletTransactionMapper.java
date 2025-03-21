package com.ruoyi.alse.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.alse.domain.AlseWalletTransaction;

/**
 * 钱包交易流水Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-10
 */
public interface AlseWalletTransactionMapper
{
    /**
     * 查询钱包交易流水
     *
     * @param transactionId 钱包交易流水主键
     * @return 钱包交易流水
     */
    public AlseWalletTransaction selectAlseWalletTransactionByTransactionId(Long transactionId);

    /**
     * 查询钱包交易流水列表
     *
     * @param alseWalletTransaction 钱包交易流水
     * @return 钱包交易流水集合
     */
    public List<AlseWalletTransaction> selectAlseWalletTransactionList(AlseWalletTransaction alseWalletTransaction);

    /**
     * 根据条件查询钱包交易记录（支持收入/支出筛选）
     *
     * @param params 查询条件
     * @return 交易记录列表
     */
    List<AlseWalletTransaction> selectWalletTransactionByCategory(Map<String, Object> params);

    /**
     * 新增钱包交易流水
     *
     * @param alseWalletTransaction 钱包交易流水
     * @return 结果
     */
    public int insertAlseWalletTransaction(AlseWalletTransaction alseWalletTransaction);

    /**
     * 修改钱包交易流水
     *
     * @param alseWalletTransaction 钱包交易流水
     * @return 结果
     */
    public int updateAlseWalletTransaction(AlseWalletTransaction alseWalletTransaction);

    /**
     * 删除钱包交易流水
     *
     * @param transactionId 钱包交易流水主键
     * @return 结果
     */
    public int deleteAlseWalletTransactionByTransactionId(Long transactionId);

    /**
     * 批量删除钱包交易流水
     *
     * @param transactionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAlseWalletTransactionByTransactionIds(Long[] transactionIds);
}

package com.ruoyi.alse.mapper;

import java.util.List;
import com.ruoyi.alse.domain.AlseOrder;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * 商品订单Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-14
 */
public interface AlseOrderMapper
{

    /**
     * 统计用户卖出的商品总数
     *
     * @param userId 用户ID
     * @return 卖出商品总数
     */
    int countSoldProductsByUser(@Param("userId") Long userId);

    /**
     * 统计用户购买的商品总数
     *
     * @param userId 用户ID
     * @return 购买商品总数
     */
    int countBoughtProductsByUser(@Param("userId") Long userId);

    /**
     * 查询商品订单
     *
     * @param orderId 商品订单主键
     * @return 商品订单
     */
    public AlseOrder selectAlseOrderByOrderId(Long orderId);

    /**
     * 查询商品订单列表
     *
     * @param alseOrder 商品订单
     * @return 商品订单集合
     */
    public List<AlseOrder> selectAlseOrderList(AlseOrder alseOrder);

    /**
     * 新增商品订单
     *
     * @param alseOrder 商品订单
     * @return 结果
     */
    public int insertAlseOrder(AlseOrder alseOrder);

    /**
     * 修改商品订单
     *
     * @param alseOrder 商品订单
     * @return 结果
     */
    public int updateAlseOrder(AlseOrder alseOrder);

    /**
     * 删除商品订单
     *
     * @param orderId 商品订单主键
     * @return 结果
     */
    public int deleteAlseOrderByOrderId(Long orderId);

    /**
     * 批量删除商品订单
     *
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAlseOrderByOrderIds(Long[] orderIds);
}

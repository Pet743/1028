package com.ruoyi.alse.service;

import java.util.List;
import com.ruoyi.alse.domain.AlseProduct;

/**
 * 商品Service接口
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
public interface IAlseProductService 
{
    /**
     * 查询商品
     * 
     * @param productId 商品主键
     * @return 商品
     */
    public AlseProduct selectAlseProductByProductId(Long productId);

    /**
     * 查询商品列表
     * 
     * @param alseProduct 商品
     * @return 商品集合
     */
    public List<AlseProduct> selectAlseProductList(AlseProduct alseProduct);

    /**
     * 新增商品
     * 
     * @param alseProduct 商品
     * @return 结果
     */
    public int insertAlseProduct(AlseProduct alseProduct);

    /**
     * 修改商品
     * 
     * @param alseProduct 商品
     * @return 结果
     */
    public int updateAlseProduct(AlseProduct alseProduct);

    /**
     * 批量删除商品
     * 
     * @param productIds 需要删除的商品主键集合
     * @return 结果
     */
    public int deleteAlseProductByProductIds(Long[] productIds);

    /**
     * 删除商品信息
     * 
     * @param productId 商品主键
     * @return 结果
     */
    public int deleteAlseProductByProductId(Long productId);
}

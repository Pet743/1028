package com.ruoyi.alse.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlseProductMapper;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.service.IAlseProductService;

/**
 * 商品Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
@Service
public class AlseProductServiceImpl implements IAlseProductService 
{
    @Autowired
    private AlseProductMapper alseProductMapper;

    @Override
    public AlseProduct selectAlseProductByProductIdForUpdate(Long productId) {
        return alseProductMapper.selectAlseProductByProductIdForUpdate(productId);
    }


    /**
     * 查询商品
     * 
     * @param productId 商品主键
     * @return 商品
     */
    @Override
    public AlseProduct selectAlseProductByProductId(Long productId)
    {
        return alseProductMapper.selectAlseProductByProductId(productId);
    }

    /**
     * 查询商品列表
     * 
     * @param alseProduct 商品
     * @return 商品
     */
    @Override
    public List<AlseProduct> selectAlseProductList(AlseProduct alseProduct)
    {
        return alseProductMapper.selectAlseProductList(alseProduct);
    }

    /**
     * 新增商品
     * 
     * @param alseProduct 商品
     * @return 结果
     */
    @Override
    public int insertAlseProduct(AlseProduct alseProduct)
    {
        alseProduct.setCreateTime(DateUtils.getNowDate());
        return alseProductMapper.insertAlseProduct(alseProduct);
    }

    /**
     * 修改商品
     * 
     * @param alseProduct 商品
     * @return 结果
     */
    @Override
    public int updateAlseProduct(AlseProduct alseProduct)
    {
        alseProduct.setUpdateTime(DateUtils.getNowDate());
        return alseProductMapper.updateAlseProduct(alseProduct);
    }

    /**
     * 批量删除商品
     * 
     * @param productIds 需要删除的商品主键
     * @return 结果
     */
    @Override
    public int deleteAlseProductByProductIds(Long[] productIds)
    {
        return alseProductMapper.deleteAlseProductByProductIds(productIds);
    }

    /**
     * 删除商品信息
     * 
     * @param productId 商品主键
     * @return 结果
     */
    @Override
    public int deleteAlseProductByProductId(Long productId)
    {
        return alseProductMapper.deleteAlseProductByProductId(productId);
    }
}

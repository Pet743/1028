package com.ruoyi.alse.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlseIndexContentMapper;
import com.ruoyi.alse.domain.AlseIndexContent;
import com.ruoyi.alse.service.IAlseIndexContentService;

/**
 * 首页内容Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
@Service
public class AlseIndexContentServiceImpl implements IAlseIndexContentService 
{
    @Autowired
    private AlseIndexContentMapper alseIndexContentMapper;

    /**
     * 查询首页内容
     * 
     * @param contentId 首页内容主键
     * @return 首页内容
     */
    @Override
    public AlseIndexContent selectAlseIndexContentByContentId(Long contentId)
    {
        return alseIndexContentMapper.selectAlseIndexContentByContentId(contentId);
    }

    /**
     * 查询首页内容列表
     * 
     * @param alseIndexContent 首页内容
     * @return 首页内容
     */
    @Override
    public List<AlseIndexContent> selectAlseIndexContentList(AlseIndexContent alseIndexContent)
    {
        return alseIndexContentMapper.selectAlseIndexContentList(alseIndexContent);
    }

    /**
     * 新增首页内容
     * 
     * @param alseIndexContent 首页内容
     * @return 结果
     */
    @Override
    public int insertAlseIndexContent(AlseIndexContent alseIndexContent)
    {
        alseIndexContent.setCreateTime(DateUtils.getNowDate());
        return alseIndexContentMapper.insertAlseIndexContent(alseIndexContent);
    }

    /**
     * 修改首页内容
     * 
     * @param alseIndexContent 首页内容
     * @return 结果
     */
    @Override
    public int updateAlseIndexContent(AlseIndexContent alseIndexContent)
    {
        alseIndexContent.setUpdateTime(DateUtils.getNowDate());
        return alseIndexContentMapper.updateAlseIndexContent(alseIndexContent);
    }

    /**
     * 批量删除首页内容
     * 
     * @param contentIds 需要删除的首页内容主键
     * @return 结果
     */
    @Override
    public int deleteAlseIndexContentByContentIds(Long[] contentIds)
    {
        return alseIndexContentMapper.deleteAlseIndexContentByContentIds(contentIds);
    }

    /**
     * 删除首页内容信息
     * 
     * @param contentId 首页内容主键
     * @return 结果
     */
    @Override
    public int deleteAlseIndexContentByContentId(Long contentId)
    {
        return alseIndexContentMapper.deleteAlseIndexContentByContentId(contentId);
    }
}

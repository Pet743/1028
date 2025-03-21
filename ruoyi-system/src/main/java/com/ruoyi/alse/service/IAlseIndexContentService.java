package com.ruoyi.alse.service;

import java.util.List;
import com.ruoyi.alse.domain.AlseIndexContent;

/**
 * 首页内容Service接口
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
public interface IAlseIndexContentService 
{
    /**
     * 查询首页内容
     * 
     * @param contentId 首页内容主键
     * @return 首页内容
     */
    public AlseIndexContent selectAlseIndexContentByContentId(Long contentId);

    /**
     * 查询首页内容列表
     * 
     * @param alseIndexContent 首页内容
     * @return 首页内容集合
     */
    public List<AlseIndexContent> selectAlseIndexContentList(AlseIndexContent alseIndexContent);

    /**
     * 新增首页内容
     * 
     * @param alseIndexContent 首页内容
     * @return 结果
     */
    public int insertAlseIndexContent(AlseIndexContent alseIndexContent);

    /**
     * 修改首页内容
     * 
     * @param alseIndexContent 首页内容
     * @return 结果
     */
    public int updateAlseIndexContent(AlseIndexContent alseIndexContent);

    /**
     * 批量删除首页内容
     * 
     * @param contentIds 需要删除的首页内容主键集合
     * @return 结果
     */
    public int deleteAlseIndexContentByContentIds(Long[] contentIds);

    /**
     * 删除首页内容信息
     * 
     * @param contentId 首页内容主键
     * @return 结果
     */
    public int deleteAlseIndexContentByContentId(Long contentId);
}

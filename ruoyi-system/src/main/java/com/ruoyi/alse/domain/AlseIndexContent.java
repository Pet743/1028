package com.ruoyi.alse.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 首页内容对象 alse_index_content
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
public class AlseIndexContent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 内容ID */
    private Long contentId;

    /** 内容类型：0-轮播图，1-通知公告 */
    @Excel(name = "内容类型：0-轮播图，1-通知公告")
    private Integer contentType;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 图片URL（轮播图时使用） */
    @Excel(name = "图片URL", readConverterExp = "轮=播图时使用")
    private String imageUrl;

    /** 链接URL（点击跳转地址） */
    @Excel(name = "链接URL", readConverterExp = "点=击跳转地址")
    private String linkUrl;

    /** 排序号（数字越小越靠前） */
    @Excel(name = "排序号", readConverterExp = "数=字越小越靠前")
    private Integer sortOrder;

    /** 开始展示时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始展示时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束展示时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束展示时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setContentId(Long contentId) 
    {
        this.contentId = contentId;
    }

    public Long getContentId() 
    {
        return contentId;
    }

    public void setContentType(Integer contentType) 
    {
        this.contentType = contentType;
    }

    public Integer getContentType() 
    {
        return contentType;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }

    public void setLinkUrl(String linkUrl) 
    {
        this.linkUrl = linkUrl;
    }

    public String getLinkUrl() 
    {
        return linkUrl;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("contentId", getContentId())
            .append("contentType", getContentType())
            .append("title", getTitle())
            .append("content", getContent())
            .append("imageUrl", getImageUrl())
            .append("linkUrl", getLinkUrl())
            .append("sortOrder", getSortOrder())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .toString();
    }
}

package com.ruoyi.alse.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.alse.domain.AlseIndexContent;
import com.ruoyi.alse.service.IAlseIndexContentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 首页内容Controller
 * 
 * @author ruoyi
 * @date 2025-03-21
 */
@RestController
@RequestMapping("/alse/content")
public class AlseIndexContentController extends BaseController
{
    @Autowired
    private IAlseIndexContentService alseIndexContentService;

    /**
     * 查询首页内容列表
     */
    @PreAuthorize("@ss.hasPermi('alse:content:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlseIndexContent alseIndexContent)
    {
        startPage();
        List<AlseIndexContent> list = alseIndexContentService.selectAlseIndexContentList(alseIndexContent);
        return getDataTable(list);
    }

    /**
     * 导出首页内容列表
     */
    @PreAuthorize("@ss.hasPermi('alse:content:export')")
    @Log(title = "首页内容", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlseIndexContent alseIndexContent)
    {
        List<AlseIndexContent> list = alseIndexContentService.selectAlseIndexContentList(alseIndexContent);
        ExcelUtil<AlseIndexContent> util = new ExcelUtil<AlseIndexContent>(AlseIndexContent.class);
        util.exportExcel(response, list, "首页内容数据");
    }

    /**
     * 获取首页内容详细信息
     */
    @PreAuthorize("@ss.hasPermi('alse:content:query')")
    @GetMapping(value = "/{contentId}")
    public AjaxResult getInfo(@PathVariable("contentId") Long contentId)
    {
        return success(alseIndexContentService.selectAlseIndexContentByContentId(contentId));
    }

    /**
     * 新增首页内容
     */
    @PreAuthorize("@ss.hasPermi('alse:content:add')")
    @Log(title = "首页内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlseIndexContent alseIndexContent)
    {
        return toAjax(alseIndexContentService.insertAlseIndexContent(alseIndexContent));
    }

    /**
     * 修改首页内容
     */
    @PreAuthorize("@ss.hasPermi('alse:content:edit')")
    @Log(title = "首页内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlseIndexContent alseIndexContent)
    {
        return toAjax(alseIndexContentService.updateAlseIndexContent(alseIndexContent));
    }

    /**
     * 删除首页内容
     */
    @PreAuthorize("@ss.hasPermi('alse:content:remove')")
    @Log(title = "首页内容", businessType = BusinessType.DELETE)
	@DeleteMapping("/{contentIds}")
    public AjaxResult remove(@PathVariable Long[] contentIds)
    {
        return toAjax(alseIndexContentService.deleteAlseIndexContentByContentIds(contentIds));
    }
}

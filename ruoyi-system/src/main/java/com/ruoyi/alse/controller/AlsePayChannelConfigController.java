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
import com.ruoyi.alse.domain.AlsePayChannelConfig;
import com.ruoyi.alse.service.IAlsePayChannelConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 支付通道配置Controller
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@RestController
@RequestMapping("/alse/config")
public class AlsePayChannelConfigController extends BaseController
{
    @Autowired
    private IAlsePayChannelConfigService alsePayChannelConfigService;

    /**
     * 查询支付通道配置列表
     */
    @PreAuthorize("@ss.hasPermi('alse:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlsePayChannelConfig alsePayChannelConfig)
    {
        startPage();
        List<AlsePayChannelConfig> list = alsePayChannelConfigService.selectAlsePayChannelConfigList(alsePayChannelConfig);
        return getDataTable(list);
    }

    /**
     * 导出支付通道配置列表
     */
    @PreAuthorize("@ss.hasPermi('alse:config:export')")
    @Log(title = "支付通道配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlsePayChannelConfig alsePayChannelConfig)
    {
        List<AlsePayChannelConfig> list = alsePayChannelConfigService.selectAlsePayChannelConfigList(alsePayChannelConfig);
        ExcelUtil<AlsePayChannelConfig> util = new ExcelUtil<AlsePayChannelConfig>(AlsePayChannelConfig.class);
        util.exportExcel(response, list, "支付通道配置数据");
    }

    /**
     * 获取支付通道配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('alse:config:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(alsePayChannelConfigService.selectAlsePayChannelConfigById(id));
    }

    /**
     * 新增支付通道配置
     */
    @PreAuthorize("@ss.hasPermi('alse:config:add')")
    @Log(title = "支付通道配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlsePayChannelConfig alsePayChannelConfig)
    {
        return toAjax(alsePayChannelConfigService.insertAlsePayChannelConfig(alsePayChannelConfig));
    }

    /**
     * 修改支付通道配置
     */
    @PreAuthorize("@ss.hasPermi('alse:config:edit')")
    @Log(title = "支付通道配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlsePayChannelConfig alsePayChannelConfig)
    {
        return toAjax(alsePayChannelConfigService.updateAlsePayChannelConfig(alsePayChannelConfig));
    }

    /**
     * 删除支付通道配置
     */
    @PreAuthorize("@ss.hasPermi('alse:config:remove')")
    @Log(title = "支付通道配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(alsePayChannelConfigService.deleteAlsePayChannelConfigByIds(ids));
    }
}

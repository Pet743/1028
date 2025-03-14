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
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户Controller
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
@RestController
@RequestMapping("/alse/user")
public class AlseUserController extends BaseController
{
    @Autowired
    private IAlseUserService alseUserService;

    /**
     * 查询用户列表
     */
    @PreAuthorize("@ss.hasPermi('alse:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlseUser alseUser)
    {
        startPage();
        List<AlseUser> list = alseUserService.selectAlseUserList(alseUser);
        return getDataTable(list);
    }

    /**
     * 导出用户列表
     */
    @PreAuthorize("@ss.hasPermi('alse:user:export')")
    @Log(title = "用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlseUser alseUser)
    {
        List<AlseUser> list = alseUserService.selectAlseUserList(alseUser);
        ExcelUtil<AlseUser> util = new ExcelUtil<AlseUser>(AlseUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 获取用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('alse:user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(alseUserService.selectAlseUserByUserId(userId));
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('alse:user:add')")
    @Log(title = "用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlseUser alseUser)
    {
        return toAjax(alseUserService.insertAlseUser(alseUser));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('alse:user:edit')")
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlseUser alseUser)
    {
        return toAjax(alseUserService.updateAlseUser(alseUser));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('alse:user:remove')")
    @Log(title = "用户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(alseUserService.deleteAlseUserByUserIds(userIds));
    }
}

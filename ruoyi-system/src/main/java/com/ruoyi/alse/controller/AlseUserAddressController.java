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
import com.ruoyi.alse.domain.AlseUserAddress;
import com.ruoyi.alse.service.IAlseUserAddressService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户地址Controller
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
@RestController
@RequestMapping("/alse/address")
public class AlseUserAddressController extends BaseController
{
    @Autowired
    private IAlseUserAddressService alseUserAddressService;

    /**
     * 查询用户地址列表
     */
    @PreAuthorize("@ss.hasPermi('alse:address:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlseUserAddress alseUserAddress)
    {
        startPage();
        List<AlseUserAddress> list = alseUserAddressService.selectAlseUserAddressList(alseUserAddress);
        return getDataTable(list);
    }

    /**
     * 导出用户地址列表
     */
    @PreAuthorize("@ss.hasPermi('alse:address:export')")
    @Log(title = "用户地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlseUserAddress alseUserAddress)
    {
        List<AlseUserAddress> list = alseUserAddressService.selectAlseUserAddressList(alseUserAddress);
        ExcelUtil<AlseUserAddress> util = new ExcelUtil<AlseUserAddress>(AlseUserAddress.class);
        util.exportExcel(response, list, "用户地址数据");
    }

    /**
     * 获取用户地址详细信息
     */
    @PreAuthorize("@ss.hasPermi('alse:address:query')")
    @GetMapping(value = "/{addressId}")
    public AjaxResult getInfo(@PathVariable("addressId") Long addressId)
    {
        return success(alseUserAddressService.selectAlseUserAddressByAddressId(addressId));
    }

    /**
     * 新增用户地址
     */
    @PreAuthorize("@ss.hasPermi('alse:address:add')")
    @Log(title = "用户地址", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlseUserAddress alseUserAddress)
    {
        return toAjax(alseUserAddressService.insertAlseUserAddress(alseUserAddress));
    }

    /**
     * 修改用户地址
     */
    @PreAuthorize("@ss.hasPermi('alse:address:edit')")
    @Log(title = "用户地址", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlseUserAddress alseUserAddress)
    {
        return toAjax(alseUserAddressService.updateAlseUserAddress(alseUserAddress));
    }

    /**
     * 删除用户地址
     */
    @PreAuthorize("@ss.hasPermi('alse:address:remove')")
    @Log(title = "用户地址", businessType = BusinessType.DELETE)
	@DeleteMapping("/{addressIds}")
    public AjaxResult remove(@PathVariable Long[] addressIds)
    {
        return toAjax(alseUserAddressService.deleteAlseUserAddressByAddressIds(addressIds));
    }
}

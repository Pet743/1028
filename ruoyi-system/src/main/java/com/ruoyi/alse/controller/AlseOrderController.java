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
import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.service.IAlseOrderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品订单Controller
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
@RestController
@RequestMapping("/alse/order")
public class AlseOrderController extends BaseController
{
    @Autowired
    private IAlseOrderService alseOrderService;

    /**
     * 查询商品订单列表
     */
    @PreAuthorize("@ss.hasPermi('alse:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlseOrder alseOrder)
    {
        startPage();
        List<AlseOrder> list = alseOrderService.selectAlseOrderList(alseOrder);
        return getDataTable(list);
    }

    /**
     * 导出商品订单列表
     */
    @PreAuthorize("@ss.hasPermi('alse:order:export')")
    @Log(title = "商品订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlseOrder alseOrder)
    {
        List<AlseOrder> list = alseOrderService.selectAlseOrderList(alseOrder);
        ExcelUtil<AlseOrder> util = new ExcelUtil<AlseOrder>(AlseOrder.class);
        util.exportExcel(response, list, "商品订单数据");
    }

    /**
     * 获取商品订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('alse:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(alseOrderService.selectAlseOrderByOrderId(orderId));
    }

    /**
     * 新增商品订单
     */
    @PreAuthorize("@ss.hasPermi('alse:order:add')")
    @Log(title = "商品订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlseOrder alseOrder)
    {
        return toAjax(alseOrderService.insertAlseOrder(alseOrder));
    }

    /**
     * 修改商品订单
     */
    @PreAuthorize("@ss.hasPermi('alse:order:edit')")
    @Log(title = "商品订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlseOrder alseOrder)
    {
        return toAjax(alseOrderService.updateAlseOrder(alseOrder));
    }

    /**
     * 删除商品订单
     */
    @PreAuthorize("@ss.hasPermi('alse:order:remove')")
    @Log(title = "商品订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(alseOrderService.deleteAlseOrderByOrderIds(orderIds));
    }
}

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
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.service.IAlseProductService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品Controller
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
@RestController
@RequestMapping("/alse/product")
public class AlseProductController extends BaseController
{
    @Autowired
    private IAlseProductService alseProductService;

    /**
     * 查询商品列表
     */
    @PreAuthorize("@ss.hasPermi('alse:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlseProduct alseProduct)
    {
        startPage();
        List<AlseProduct> list = alseProductService.selectAlseProductList(alseProduct);
        return getDataTable(list);
    }

    /**
     * 导出商品列表
     */
    @PreAuthorize("@ss.hasPermi('alse:product:export')")
    @Log(title = "商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlseProduct alseProduct)
    {
        List<AlseProduct> list = alseProductService.selectAlseProductList(alseProduct);
        ExcelUtil<AlseProduct> util = new ExcelUtil<AlseProduct>(AlseProduct.class);
        util.exportExcel(response, list, "商品数据");
    }

    /**
     * 获取商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('alse:product:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") Long productId)
    {
        return success(alseProductService.selectAlseProductByProductId(productId));
    }

    /**
     * 新增商品
     */
    @PreAuthorize("@ss.hasPermi('alse:product:add')")
    @Log(title = "商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlseProduct alseProduct)
    {
        return toAjax(alseProductService.insertAlseProduct(alseProduct));
    }

    /**
     * 修改商品
     */
    @PreAuthorize("@ss.hasPermi('alse:product:edit')")
    @Log(title = "商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlseProduct alseProduct)
    {
        return toAjax(alseProductService.updateAlseProduct(alseProduct));
    }

    /**
     * 删除商品
     */
    @PreAuthorize("@ss.hasPermi('alse:product:remove')")
    @Log(title = "商品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(alseProductService.deleteAlseProductByProductIds(productIds));
    }
}

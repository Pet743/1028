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
import com.ruoyi.alse.domain.AlseWalletTransaction;
import com.ruoyi.alse.service.IAlseWalletTransactionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 钱包交易流水Controller
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
@RestController
@RequestMapping("/alse/transaction")
public class AlseWalletTransactionController extends BaseController
{
    @Autowired
    private IAlseWalletTransactionService alseWalletTransactionService;

    /**
     * 查询钱包交易流水列表
     */
    @PreAuthorize("@ss.hasPermi('alse:transaction:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlseWalletTransaction alseWalletTransaction)
    {
        startPage();
        List<AlseWalletTransaction> list = alseWalletTransactionService.selectAlseWalletTransactionList(alseWalletTransaction);
        return getDataTable(list);
    }

    /**
     * 导出钱包交易流水列表
     */
    @PreAuthorize("@ss.hasPermi('alse:transaction:export')")
    @Log(title = "钱包交易流水", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlseWalletTransaction alseWalletTransaction)
    {
        List<AlseWalletTransaction> list = alseWalletTransactionService.selectAlseWalletTransactionList(alseWalletTransaction);
        ExcelUtil<AlseWalletTransaction> util = new ExcelUtil<AlseWalletTransaction>(AlseWalletTransaction.class);
        util.exportExcel(response, list, "钱包交易流水数据");
    }

    /**
     * 获取钱包交易流水详细信息
     */
    @PreAuthorize("@ss.hasPermi('alse:transaction:query')")
    @GetMapping(value = "/{transactionId}")
    public AjaxResult getInfo(@PathVariable("transactionId") Long transactionId)
    {
        return success(alseWalletTransactionService.selectAlseWalletTransactionByTransactionId(transactionId));
    }

    /**
     * 新增钱包交易流水
     */
    @PreAuthorize("@ss.hasPermi('alse:transaction:add')")
    @Log(title = "钱包交易流水", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlseWalletTransaction alseWalletTransaction)
    {
        return toAjax(alseWalletTransactionService.insertAlseWalletTransaction(alseWalletTransaction));
    }

    /**
     * 修改钱包交易流水
     */
    @PreAuthorize("@ss.hasPermi('alse:transaction:edit')")
    @Log(title = "钱包交易流水", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlseWalletTransaction alseWalletTransaction)
    {
        return toAjax(alseWalletTransactionService.updateAlseWalletTransaction(alseWalletTransaction));
    }

    /**
     * 删除钱包交易流水
     */
    @PreAuthorize("@ss.hasPermi('alse:transaction:remove')")
    @Log(title = "钱包交易流水", businessType = BusinessType.DELETE)
	@DeleteMapping("/{transactionIds}")
    public AjaxResult remove(@PathVariable Long[] transactionIds)
    {
        return toAjax(alseWalletTransactionService.deleteAlseWalletTransactionByTransactionIds(transactionIds));
    }
}

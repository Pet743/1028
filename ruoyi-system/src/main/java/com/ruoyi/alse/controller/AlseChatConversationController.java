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
import com.ruoyi.alse.domain.AlseChatConversation;
import com.ruoyi.alse.service.IAlseChatConversationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 聊天会话Controller
 * 
 * @author ruoyi
 * @date 2025-03-19
 */
@RestController
@RequestMapping("/alse/conversation")
public class AlseChatConversationController extends BaseController
{
    @Autowired
    private IAlseChatConversationService alseChatConversationService;

    /**
     * 查询聊天会话列表
     */
    @PreAuthorize("@ss.hasPermi('alse:conversation:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlseChatConversation alseChatConversation)
    {
        startPage();
        List<AlseChatConversation> list = alseChatConversationService.selectAlseChatConversationList(alseChatConversation);
        return getDataTable(list);
    }

    /**
     * 导出聊天会话列表
     */
    @PreAuthorize("@ss.hasPermi('alse:conversation:export')")
    @Log(title = "聊天会话", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlseChatConversation alseChatConversation)
    {
        List<AlseChatConversation> list = alseChatConversationService.selectAlseChatConversationList(alseChatConversation);
        ExcelUtil<AlseChatConversation> util = new ExcelUtil<AlseChatConversation>(AlseChatConversation.class);
        util.exportExcel(response, list, "聊天会话数据");
    }

    /**
     * 获取聊天会话详细信息
     */
    @PreAuthorize("@ss.hasPermi('alse:conversation:query')")
    @GetMapping(value = "/{conversationId}")
    public AjaxResult getInfo(@PathVariable("conversationId") Long conversationId)
    {
        return success(alseChatConversationService.selectAlseChatConversationByConversationId(conversationId));
    }

    /**
     * 新增聊天会话
     */
    @PreAuthorize("@ss.hasPermi('alse:conversation:add')")
    @Log(title = "聊天会话", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlseChatConversation alseChatConversation)
    {
        return toAjax(alseChatConversationService.insertAlseChatConversation(alseChatConversation));
    }

    /**
     * 修改聊天会话
     */
    @PreAuthorize("@ss.hasPermi('alse:conversation:edit')")
    @Log(title = "聊天会话", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlseChatConversation alseChatConversation)
    {
        return toAjax(alseChatConversationService.updateAlseChatConversation(alseChatConversation));
    }

    /**
     * 删除聊天会话
     */
    @PreAuthorize("@ss.hasPermi('alse:conversation:remove')")
    @Log(title = "聊天会话", businessType = BusinessType.DELETE)
	@DeleteMapping("/{conversationIds}")
    public AjaxResult remove(@PathVariable Long[] conversationIds)
    {
        return toAjax(alseChatConversationService.deleteAlseChatConversationByConversationIds(conversationIds));
    }
}

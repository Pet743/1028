package com.ruoyi.uni.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.domain.AlseWalletTransaction;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.alse.service.IAlseWalletTransactionService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.uni.converter.WalletTransactionConverter;
import com.ruoyi.uni.model.DTO.request.wallet.WalletTransactionQueryDTO;
import com.ruoyi.uni.model.DTO.respone.wallet.WalletBalanceResponseDTO;
import com.ruoyi.uni.model.DTO.respone.wallet.WalletTransactionResponseDTO;
import com.ruoyi.uni.model.Enum.DateRangeTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@Slf4j
@RestController
@RequestMapping("/api/wallet/transaction")
@Api(tags = "钱包交易流水接口")
public class UniWalletTransactionController {

    @Autowired
    private IAlseWalletTransactionService walletTransactionService;

    @Autowired
    private IAlseUserService alseUserService;

    @Autowired
    private WalletTransactionConverter walletTransactionConverter;

    /**
     * 查询钱包余额和交易统计
     */
    @GetMapping("/balance")
    @CheckToken
    @ApiOperation("查询钱包余额和交易统计")
    public AjaxResult getWalletBalance(@RequestParam Long userId) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(userId);
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 使用转换器处理数据
            WalletBalanceResponseDTO balanceDTO = walletTransactionConverter.toBalanceResponseDTO(user);

            // 检查余额一致性并记录警告日志
            if (!balanceDTO.getBalanceCorrect()) {
                log.warn("用户{}的钱包余额({})与计算值({})不一致",
                        userId,
                        balanceDTO.getWalletBalance(),
                        balanceDTO.getTotalIncome() + " - " + balanceDTO.getTotalExpense());
            }

            return AjaxResult.success("查询成功", balanceDTO);
        } catch (Exception e) {
            log.error("查询钱包余额失败", e);
            return AjaxResult.error("查询钱包余额失败: " + e.getMessage());
        }
    }

    /**
     * 查询钱包交易流水
     */
    @GetMapping("/list")
    @CheckToken
    @ApiOperation("查询钱包交易流水")
    public TableDataInfo list(WalletTransactionQueryDTO queryDTO) {
        try {
            // 参数验证
            if (queryDTO.getUserId() == null) {
                TableDataInfo errorData = new TableDataInfo();
                errorData.setCode(500);
                errorData.setMsg("用户ID不能为空");
                return errorData;
            }

            Integer pageNum = queryDTO.getPageNum();
            Integer pageSize = queryDTO.getPageSize();

            // 确保分页参数有效
            if (pageNum == null || pageNum < 1) {
                pageNum = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;  // 默认每页10条
            }

            PageHelper.startPage(pageNum, pageSize);

            // 计算日期范围
            Date[] dateRange;

            // 如果设置了查询类型，使用对应的日期范围
            if (queryDTO.getQueryType() != null) {
                // 使用枚举处理日期范围查询
                DateRangeTypeEnum dateRangeTypeEnum = DateRangeTypeEnum.getByValue(queryDTO.getQueryType());

                // 准备自定义日期参数
                Date[] customDates = null;
                if (queryDTO.getStartDate() != null && queryDTO.getEndDate() != null) {
                    customDates = new Date[] { queryDTO.getStartDate(), queryDTO.getEndDate() };
                }

                // 计算日期范围
                dateRange = dateRangeTypeEnum.calculateDateRange(queryDTO.getCustomDays(), customDates);
            } else {
                // 默认使用TODAY的计算逻辑
                dateRange = DateRangeTypeEnum.TODAY.calculateDateRange(null, null);
            }

            Date startTime = dateRange[0];
            Date endTime = dateRange[1];

            // 创建查询参数Map
            Map<String, Object> params = new HashMap<>();
            params.put("userId", queryDTO.getUserId());
            params.put("beginTransactionTime", startTime);
            params.put("endTransactionTime", endTime);

            // 设置交易类别筛选参数
            if (queryDTO.getTransactionCategory() != null) {
                params.put("transactionCategory", queryDTO.getTransactionCategory());
            }

            // 调用新的Service方法查询
            List<AlseWalletTransaction> list = walletTransactionService.selectWalletTransactionByCategory(params);

            // 创建PageInfo对象，用于获取总记录数和页数信息
            PageInfo<AlseWalletTransaction> pageInfo = new PageInfo<>(list);

            // 如果当前页码超出总页数且总页数不为0，则返回空列表
            if (pageNum > pageInfo.getPages() && pageInfo.getPages() > 0) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(200);
                rspData.setRows(new ArrayList<>());
                rspData.setMsg("查询成功");
                rspData.setTotal(pageInfo.getTotal());
                return rspData;
            }

            // 使用转换器将实体列表转换为响应DTO列表
            List<WalletTransactionResponseDTO> resultList = walletTransactionConverter.toResponseDTOList(list);

            // 返回结果
            return getDataTable(resultList, pageInfo.getTotal());
        } catch (Exception e) {
            log.error("查询钱包交易流水失败", e);
            TableDataInfo errorData = new TableDataInfo();
            errorData.setCode(500);
            errorData.setMsg("查询钱包交易流水失败: " + e.getMessage());
            return errorData;
        }
    }


    /**
     * 封装分页数据
     */
    protected TableDataInfo getDataTable(List<?> list, long total) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(total);
        return rspData;
    }
}
package com.ruoyi.uni.controller;

import com.ruoyi.alse.domain.AlseWalletTransaction;
import com.ruoyi.alse.service.IAlseWalletTransactionService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.uni.model.DTO.request.wallet.WalletTransactionQueryDTO;
import com.ruoyi.uni.model.Enum.DateRangeTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/wallet/transaction")
@Api(tags = "钱包交易流水接口")
public class UniWalletTransactionController {

    @Autowired
    private IAlseWalletTransactionService walletTransactionService;

    @GetMapping("/list")
    @CheckToken
    @ApiOperation("查询钱包交易流水")
    public TableDataInfo list(WalletTransactionQueryDTO queryDTO) {
        // 使用PageUtils工具类进行分页
        PageUtils.startPage();

        // 使用枚举处理日期范围查询
        DateRangeTypeEnum dateRangeTypeEnum = DateRangeTypeEnum.getByValue(queryDTO.getQueryType());

        // 准备自定义日期参数
        Date[] customDates = null;
        if (queryDTO.getStartDate() != null && queryDTO.getEndDate() != null) {
            customDates = new Date[] { queryDTO.getStartDate(), queryDTO.getEndDate() };
        }

        // 计算日期范围
        Date[] dateRange = dateRangeTypeEnum.calculateDateRange(queryDTO.getCustomDays(), customDates);
        Date startTime = dateRange[0];
        Date endTime = dateRange[1];

        // 创建查询对象并设置查询条件
        AlseWalletTransaction query = new AlseWalletTransaction();
        query.setUserId(queryDTO.getUserId());

        // 设置查询参数
        query.setParams(new HashMap<>());
        query.getParams().put("beginTransactionTime", startTime);
        query.getParams().put("endTransactionTime", endTime);

        // 调用Service查询
        List<AlseWalletTransaction> list = walletTransactionService.selectAlseWalletTransactionList(query);

        // 返回结果
        return getDataTable(list);
    }

    /**
     * 封装分页数据
     */
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(new com.github.pagehelper.PageInfo(list).getTotal());
        return rspData;
    }
}

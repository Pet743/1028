package com.ruoyi.uni.controller;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.domain.AlseWalletTransaction;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.alse.service.IAlseWalletTransactionService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.converter.WalletTransactionConverter;
import com.ruoyi.uni.model.DTO.request.wallet.PaymentMethodRequestDTO;
import com.ruoyi.uni.model.DTO.request.wallet.PaymentMethodResponseDTO;
import com.ruoyi.uni.model.DTO.request.wallet.WalletTransactionQueryDTO;
import com.ruoyi.uni.model.DTO.respone.wallet.WalletBalanceResponseDTO;
import com.ruoyi.uni.model.DTO.respone.wallet.WalletTransactionResponseDTO;
import com.ruoyi.uni.model.Enum.DateRangeTypeEnum;
import com.ruoyi.uni.model.Enum.PaymentMethodEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Slf4j
@RestController
@RequestMapping("/api/wallet/transaction")
@Api(tags = "钱包交易接口")
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
     * 添加收款方式
     */
    @PostMapping("/payment-method")
    @ApiOperation("添加收款方式")
    public AjaxResult addPaymentMethod(@Validated @RequestBody PaymentMethodRequestDTO requestDTO) {
        try {
            if (Objects.isNull(requestDTO.getUserId())){
                return AjaxResult.error("用户不存在");
            }

            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(requestDTO.getUserId());
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 获取用户的收款方式列表
            List<PaymentMethodResponseDTO> paymentMethods = getPaymentMethodList(user);

            // 生成新的收款方式ID
            String newPaymentMethodId = UUID.randomUUID().toString().replace("-", "");

            // 获取支付方式枚举
            PaymentMethodEnum paymentMethodEnum = PaymentMethodEnum.getByCode(requestDTO.getPaymentType());

            // 创建新的收款方式对象
            PaymentMethodResponseDTO newPaymentMethod = new PaymentMethodResponseDTO();
            newPaymentMethod.setPaymentMethodId(newPaymentMethodId);
            newPaymentMethod.setPaymentType(paymentMethodEnum.getCode());
            newPaymentMethod.setPaymentTypeName(paymentMethodEnum.getDesc());
            newPaymentMethod.setPayeeName(requestDTO.getPayeeName());
            newPaymentMethod.setPaymentAccount(requestDTO.getPaymentAccount());
            newPaymentMethod.setIsDefault(requestDTO.getIsDefault());

            // 如果设置为默认，则将其他收款方式的默认状态设为0
            if (requestDTO.getIsDefault() == 1) {
                for (PaymentMethodResponseDTO method : paymentMethods) {
                    method.setIsDefault(0);
                }
            }

            // 添加新的收款方式
            paymentMethods.add(newPaymentMethod);

            // 更新用户的收款方式JSON
            updateUserPaymentMethods(requestDTO.getUserId(), paymentMethods);

            // 如果设置为默认，更新用户默认收款方式
            if (requestDTO.getIsDefault() == 1) {
                updateUserDefaultPaymentMethod(requestDTO.getUserId(), paymentMethodEnum.getCode(), requestDTO.getPaymentAccount());
            }

            return AjaxResult.success("添加收款方式成功", newPaymentMethod);
        } catch (Exception e) {
            log.error("添加收款方式失败", e);
            return AjaxResult.error("添加收款方式失败: " + e.getMessage());
        }
    }

    /**
     * 编辑收款方式
     */
    @PutMapping("/payment-method")
    @ApiOperation("编辑收款方式")
    public AjaxResult updatePaymentMethod(@Validated @RequestBody PaymentMethodRequestDTO requestDTO) {
        try {
            if (Objects.isNull(requestDTO.getUserId())){
                return AjaxResult.error("用户不存在");
            }
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(requestDTO.getUserId());
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 检查收款方式ID是否存在
            if (StringUtils.isEmpty(requestDTO.getPaymentMethodId())) {
                return AjaxResult.error("收款方式ID不能为空");
            }

            // 获取用户的收款方式列表
            List<PaymentMethodResponseDTO> paymentMethods = getPaymentMethodList(user);

            // 获取支付方式枚举
            PaymentMethodEnum paymentMethodEnum = PaymentMethodEnum.getByCode(requestDTO.getPaymentType());

            // 查找并更新指定的收款方式
            boolean found = false;
            for (PaymentMethodResponseDTO method : paymentMethods) {
                if (method.getPaymentMethodId().equals(requestDTO.getPaymentMethodId())) {
                    method.setPaymentType(paymentMethodEnum.getCode());
                    method.setPaymentTypeName(paymentMethodEnum.getDesc());
                    method.setPayeeName(requestDTO.getPayeeName());
                    method.setPaymentAccount(requestDTO.getPaymentAccount());
                    method.setIsDefault(requestDTO.getIsDefault());
                    found = true;
                } else if (requestDTO.getIsDefault() == 1) {
                    // 如果设置为默认，则将其他收款方式的默认状态设为0
                    method.setIsDefault(0);
                }
            }

            if (!found) {
                return AjaxResult.error("收款方式不存在");
            }

            // 更新用户的收款方式JSON
            updateUserPaymentMethods(requestDTO.getUserId(), paymentMethods);

            // 如果设置为默认，更新用户默认收款方式
            if (requestDTO.getIsDefault() == 1) {
                updateUserDefaultPaymentMethod(requestDTO.getUserId(), paymentMethodEnum.getCode(), requestDTO.getPaymentAccount());
            }

            return AjaxResult.success("更新收款方式成功");
        } catch (Exception e) {
            log.error("更新收款方式失败", e);
            return AjaxResult.error("更新收款方式失败: " + e.getMessage());
        }
    }

    /**
     * 设置默认收款方式
     */
    @PutMapping("/payment-method/default/{paymentMethodId}")
    @ApiOperation("设置默认收款方式")
    public AjaxResult setDefaultPaymentMethod(@PathVariable("paymentMethodId") String paymentMethodId, @RequestParam Long userId) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(userId);
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 获取用户的收款方式列表
            List<PaymentMethodResponseDTO> paymentMethods = getPaymentMethodList(user);

            // 查找并设置默认收款方式
            boolean found = false;
            Integer paymentType = null;
            String paymentAccount = null;

            for (PaymentMethodResponseDTO method : paymentMethods) {
                if (method.getPaymentMethodId().equals(paymentMethodId)) {
                    method.setIsDefault(1);
                    found = true;
                    paymentType = method.getPaymentType();
                    paymentAccount = method.getPaymentAccount();
                } else {
                    method.setIsDefault(0);
                }
            }

            if (!found) {
                return AjaxResult.error("收款方式不存在");
            }

            // 更新用户的收款方式JSON
            updateUserPaymentMethods(userId, paymentMethods);

            // 更新用户默认收款方式
            updateUserDefaultPaymentMethod(userId, paymentType, paymentAccount);

            return AjaxResult.success("设置默认收款方式成功");
        } catch (Exception e) {
            log.error("设置默认收款方式失败", e);
            return AjaxResult.error("设置默认收款方式失败: " + e.getMessage());
        }
    }

    /**
     * 删除收款方式
     */
    @DeleteMapping("/payment-method/{paymentMethodId}")
    @ApiOperation("删除收款方式")
    public AjaxResult deletePaymentMethod(@PathVariable("paymentMethodId") String paymentMethodId, @RequestParam Long userId) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(userId);
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 获取用户的收款方式列表
            List<PaymentMethodResponseDTO> paymentMethods = getPaymentMethodList(user);

            // 查找并删除指定的收款方式
            boolean found = false;
            boolean isDefault = false;
            Iterator<PaymentMethodResponseDTO> iterator = paymentMethods.iterator();

            while (iterator.hasNext()) {
                PaymentMethodResponseDTO method = iterator.next();
                if (method.getPaymentMethodId().equals(paymentMethodId)) {
                    isDefault = method.getIsDefault() == 1;
                    iterator.remove();
                    found = true;
                    break;
                }
            }

            if (!found) {
                return AjaxResult.error("收款方式不存在");
            }

            // 更新用户的收款方式JSON
            updateUserPaymentMethods(userId, paymentMethods);

            // 如果删除的是默认收款方式，且还有其他收款方式，则设置第一个为默认
            if (isDefault && !paymentMethods.isEmpty()) {
                PaymentMethodResponseDTO newDefault = paymentMethods.get(0);
                newDefault.setIsDefault(1);

                // 再次更新用户的收款方式JSON
                updateUserPaymentMethods(userId, paymentMethods);

                // 更新用户默认收款方式
                updateUserDefaultPaymentMethod(userId, newDefault.getPaymentType(), newDefault.getPaymentAccount());
            } else if (isDefault) {
                // 如果没有其他收款方式，清空默认收款方式
                updateUserDefaultPaymentMethod(userId, null, null);
            }

            return AjaxResult.success("删除收款方式成功");
        } catch (Exception e) {
            log.error("删除收款方式失败", e);
            return AjaxResult.error("删除收款方式失败: " + e.getMessage());
        }
    }

    /**
     * 获取收款方式列表
     */
    @GetMapping("/payment-method/list")
    @ApiOperation("获取收款方式列表")
    public AjaxResult getPaymentMethodList(@RequestParam Long userId) {
        try {
            // 查询用户信息
            AlseUser user = alseUserService.selectAlseUserByUserId(userId);
            if (user == null) {
                return AjaxResult.error("用户不存在");
            }

            // 获取用户的收款方式列表
            List<PaymentMethodResponseDTO> paymentMethods = getPaymentMethodList(user);

            return AjaxResult.success(paymentMethods);
        } catch (Exception e) {
            log.error("获取收款方式列表失败", e);
            return AjaxResult.error("获取收款方式列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的收款方式列表
     */
    private List<PaymentMethodResponseDTO> getPaymentMethodList(AlseUser user) {
        String paymentMethodIds = user.getPaymentMethodIds();
        if (StringUtils.isEmpty(paymentMethodIds)) {
            return new ArrayList<>();
        }

        try {
            return JSON.parseArray(paymentMethodIds, PaymentMethodResponseDTO.class);
        } catch (Exception e) {
            log.error("解析用户收款方式JSON失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 更新用户的收款方式JSON
     */
    private void updateUserPaymentMethods(Long userId, List<PaymentMethodResponseDTO> paymentMethods) {
        AlseUser updateUser = new AlseUser();
        updateUser.setUserId(userId);
        updateUser.setPaymentMethodIds(JSON.toJSONString(paymentMethods));
        alseUserService.updateAlseUser(updateUser);
    }

    /**
     * 更新用户默认收款方式
     */
    private void updateUserDefaultPaymentMethod(Long userId, Integer paymentMethod, String paymentAccount) {
        AlseUser updateUser = new AlseUser();
        updateUser.setUserId(userId);
        updateUser.setPaymentMethod(paymentMethod);
        updateUser.setPaymentAccount(paymentAccount);
        alseUserService.updateAlseUser(updateUser);
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
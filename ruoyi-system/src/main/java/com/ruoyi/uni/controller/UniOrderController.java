package com.ruoyi.uni.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.service.IAlseOrderService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.uni.converter.OrderConverter;
import com.ruoyi.uni.model.DTO.request.order.*;
import com.ruoyi.uni.model.DTO.respone.order.OrderDetailResponseDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderResponseDTO;
import com.ruoyi.uni.model.DTO.respone.order.PaymentResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单管理接口
 */
@RestController
@Slf4j
@RequestMapping("/api/order")
@Api(tags = "订单管理接口")
public class UniOrderController {

    @Autowired
    private IAlseOrderService alseOrderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    @CheckToken
    @ApiOperation("创建订单")
    public AjaxResult createOrder(@RequestBody @Validated CreateOrderRequestDTO requestDTO) {
        try {
            if (requestDTO.getUserId() == null) {
                return AjaxResult.error("用户ID不能为空");
            }

            // 创建订单并处理支付
            PaymentResultDTO paymentResult = alseOrderService.createOrder(requestDTO, requestDTO.getUserId());

            // 根据支付状态返回不同的消息
            if (paymentResult.getPaymentStatus() == 2) { // 已支付成功
                return AjaxResult.success("订单创建并支付成功", paymentResult);
            } else {
                return AjaxResult.success("订单创建成功，请继续完成支付", paymentResult);
            }
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取订单列表
     */
    @PostMapping("/list")
    @CheckToken
    @ApiOperation("获取订单列表(买家）")
    public TableDataInfo getOrderList(@RequestBody OrderListRequestDTO requestDTO) {
        try {
            // 获取用户ID
            Long userId = requestDTO.getUserId();
            if (userId == null) {
                TableDataInfo errorData = new TableDataInfo();
                errorData.setCode(500);
                errorData.setMsg("用户ID不能为空");
                return errorData;
            }

            // 处理分页参数
            Integer pageNum = requestDTO.getPageNum();
            Integer pageSize = requestDTO.getPageSize();

            if (pageNum == null || pageNum < 1) {
                pageNum = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }

            // 开始分页
            PageHelper.startPage(pageNum, pageSize);

            // 构建查询条件
            AlseOrder queryParam = new AlseOrder();
            queryParam.setBuyerId(userId);

            // 如果不是查询全部，设置订单状态
            if (requestDTO.getOrderStatus() != null && requestDTO.getOrderStatus() != 0) {
                queryParam.setOrderStatus(requestDTO.getOrderStatus());
            }

            // 查询订单列表
            List<AlseOrder> orderList = alseOrderService.selectAlseOrderList(queryParam);

            // 创建PageInfo对象，用于获取总记录数和页数信息
            PageInfo<AlseOrder> pageInfo = new PageInfo<>(orderList);

            // 如果当前页码超出总页数且总页数不为0，则返回空列表
            if (pageNum > pageInfo.getPages() && pageInfo.getPages() > 0) {
                return getDataTable(new ArrayList<>(), pageInfo.getTotal());
            }

            // 转换为响应DTO
            List<OrderResponseDTO> responseList = OrderConverter.convertToOrderResponseDTOList(orderList);

            return getDataTable(responseList, pageInfo.getTotal());
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            TableDataInfo errorData = new TableDataInfo();
            errorData.setCode(500);
            errorData.setMsg("获取订单列表失败：" + e.getMessage());
            return errorData;
        }
    }

    /**
     * 获取卖家订单列表
     */
    @PostMapping("/seller/list")
    @CheckToken
    @ApiOperation("获取卖家订单列表")
    public TableDataInfo getSellerOrderList(@RequestBody OrderListRequestDTO requestDTO) {
        try {
            // 获取用户ID
            Long userId = requestDTO.getUserId();
            if (userId == null) {
                TableDataInfo errorData = new TableDataInfo();
                errorData.setCode(500);
                errorData.setMsg("用户ID不能为空");
                return errorData;
            }

            // 处理分页参数
            Integer pageNum = requestDTO.getPageNum();
            Integer pageSize = requestDTO.getPageSize();

            if (pageNum == null || pageNum < 1) {
                pageNum = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }

            // 开始分页
            PageHelper.startPage(pageNum, pageSize);

            // 构建查询条件
            AlseOrder queryParam = new AlseOrder();
            queryParam.setSellerId(userId);

            // 如果不是查询全部，设置订单状态
            if (requestDTO.getOrderStatus() != null && requestDTO.getOrderStatus() != 0) {
                queryParam.setOrderStatus(requestDTO.getOrderStatus());
            }

            // 查询订单列表
            List<AlseOrder> orderList = alseOrderService.selectAlseOrderList(queryParam);

            // 创建PageInfo对象，用于获取总记录数和页数信息
            PageInfo<AlseOrder> pageInfo = new PageInfo<>(orderList);

            // 如果当前页码超出总页数且总页数不为0，则返回空列表
            if (pageNum > pageInfo.getPages() && pageInfo.getPages() > 0) {
                return getDataTable(new ArrayList<>(), pageInfo.getTotal());
            }

            // 转换为响应DTO
            List<OrderResponseDTO> responseList = OrderConverter.convertToOrderResponseDTOList(orderList);

            return getDataTable(responseList, pageInfo.getTotal());
        } catch (Exception e) {
            log.error("获取卖家订单列表失败", e);
            TableDataInfo errorData = new TableDataInfo();
            errorData.setCode(500);
            errorData.setMsg("获取卖家订单列表失败：" + e.getMessage());
            return errorData;
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{orderId}")
    @CheckToken
    @ApiOperation("获取订单详情")
    public AjaxResult getOrderDetail(@PathVariable Long orderId, @RequestParam Long userId) {
        try {
            if (userId == null) {
                return AjaxResult.error("用户ID不能为空");
            }

            // 查询订单详情
            OrderDetailResponseDTO responseDTO = alseOrderService.getOrderDetail(orderId, userId);

            return AjaxResult.success(responseDTO);
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    @CheckToken
    @ApiOperation("取消订单")
    @Log(title = "取消订单", businessType = BusinessType.UPDATE)
    public AjaxResult cancelOrder(@RequestBody CancelOrderRequestDTO requestDTO) {
        try {
            if (requestDTO.getUserId() == null || requestDTO.getOrderId() == null) {
                return AjaxResult.error("用户ID和订单ID不能为空");
            }

            // 取消订单
            boolean result = alseOrderService.cancelOrder(requestDTO.getOrderId(), requestDTO.getUserId());

            if (result) {
                return AjaxResult.success("取消订单成功");
            } else {
                return AjaxResult.error("取消订单失败");
            }
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay")
    @CheckToken
    @ApiOperation("支付订单")
    @Log(title = "支付订单", businessType = BusinessType.UPDATE)
    public AjaxResult payOrder(@RequestBody @Validated PayOrderRequestDTO requestDTO) {
        try {
            if (requestDTO.getUserId() == null || requestDTO.getOrderId() == null) {
                return AjaxResult.error("用户ID和订单ID不能为空");
            }

            // 支付订单
            boolean result = alseOrderService.payOrder(requestDTO.getOrderId(), requestDTO.getUserId(), requestDTO);

            if (result) {
                return AjaxResult.success("支付成功");
            } else {
                return AjaxResult.error("支付失败");
            }
        } catch (Exception e) {
            log.error("支付订单失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 发货
     */
    @PostMapping("/ship")
    @CheckToken
    @ApiOperation("发货")
    @Log(title = "订单发货", businessType = BusinessType.UPDATE)
    public AjaxResult shipOrder(@RequestBody @Validated ShipOrderRequestDTO requestDTO) {
        try {
            if (requestDTO.getUserId() == null || requestDTO.getOrderId() == null) {
                return AjaxResult.error("用户ID和订单ID不能为空");
            }

            // 发货
            boolean result = alseOrderService.shipOrder(requestDTO.getOrderId(), requestDTO.getUserId(), requestDTO);

            if (result) {
                return AjaxResult.success("发货成功");
            } else {
                return AjaxResult.error("发货失败");
            }
        } catch (Exception e) {
            log.error("订单发货失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirm")
    @CheckToken
    @ApiOperation("确认收货")
    @Log(title = "确认收货", businessType = BusinessType.UPDATE)
    public AjaxResult confirmReceipt(@RequestBody ConfirmReceiptRequestDTO requestDTO) {
        try {
            if (requestDTO.getUserId() == null || requestDTO.getOrderId() == null) {
                return AjaxResult.error("用户ID和订单ID不能为空");
            }

            // 确认收货
            boolean result = alseOrderService.confirmReceipt(requestDTO.getOrderId(), requestDTO.getUserId());

            if (result) {
                return AjaxResult.success("确认收货成功");
            } else {
                return AjaxResult.error("确认收货失败");
            }
        } catch (Exception e) {
            log.error("确认收货失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 封装分页数据（带总记录数）
     */
    protected TableDataInfo getDataTable(List<?> list, long total) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(total);
        return rspData;
    }

    /**
     * 封装分页数据（兼容原有方法）
     */
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        if (list != null) {
            rspData.setTotal(new PageInfo(list).getTotal());
        } else {
            rspData.setTotal(0);
        }
        return rspData;
    }
}

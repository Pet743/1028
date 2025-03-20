package com.ruoyi.uni.controller;

import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.service.IAlseOrderService;
import com.ruoyi.common.annotation.CheckToken;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.uni.converter.OrderConverter;
import com.ruoyi.uni.model.DTO.request.order.CreateOrderRequestDTO;
import com.ruoyi.uni.model.DTO.request.order.OrderListRequestDTO;
import com.ruoyi.uni.model.DTO.request.order.PayOrderRequestDTO;
import com.ruoyi.uni.model.DTO.request.order.ShipOrderRequestDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderDetailResponseDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderResponseDTO;
import com.ruoyi.uni.model.DTO.respone.order.PaymentResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.pagehelper.page.PageMethod.startPage;

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
    @ApiOperation("获取订单列表")
    public TableDataInfo getOrderList(@RequestBody OrderListRequestDTO requestDTO) {
        try {
            // 获取当前登录用户ID
            Long userId = SecurityUtils.getUserId();

            // 开始分页
            startPage(requestDTO.getPageNum(), requestDTO.getPageSize());

            // 构建查询条件
            AlseOrder queryParam = new AlseOrder();
            queryParam.setBuyerId(userId);

            // 如果不是查询全部，设置订单状态
            if (requestDTO.getOrderStatus() != null && requestDTO.getOrderStatus() != 0) {
                queryParam.setOrderStatus(requestDTO.getOrderStatus());
            }

            // 查询订单列表
            List<AlseOrder> orderList = alseOrderService.selectAlseOrderList(queryParam);

            // 转换为响应DTO
            List<OrderResponseDTO> responseList = OrderConverter.convertToOrderResponseDTOList(orderList);

            return getDataTable(responseList);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return getDataTable(null);
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
            // 获取当前登录用户ID
            Long userId = SecurityUtils.getUserId();

            // 开始分页
            startPage(requestDTO.getPageNum(), requestDTO.getPageSize());

            // 构建查询条件
            AlseOrder queryParam = new AlseOrder();
            queryParam.setSellerId(userId);

            // 如果不是查询全部，设置订单状态
            if (requestDTO.getOrderStatus() != null && requestDTO.getOrderStatus() != 0) {
                queryParam.setOrderStatus(requestDTO.getOrderStatus());
            }

            // 查询订单列表
            List<AlseOrder> orderList = alseOrderService.selectAlseOrderList(queryParam);

            // 转换为响应DTO
            List<OrderResponseDTO> responseList = OrderConverter.convertToOrderResponseDTOList(orderList);

            return getDataTable(responseList);
        } catch (Exception e) {
            log.error("获取卖家订单列表失败", e);
            return getDataTable(null);
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{orderId}")
    @CheckToken
    @ApiOperation("获取订单详情")
    public AjaxResult getOrderDetail(@PathVariable Long orderId) {
        try {
            // 获取当前登录用户ID
            Long userId = SecurityUtils.getUserId();

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
    @PostMapping("/cancel/{orderId}")
    @CheckToken
    @ApiOperation("取消订单")
    @Log(title = "取消订单", businessType = BusinessType.UPDATE)
    public AjaxResult cancelOrder(@PathVariable Long orderId) {
        try {
            // 获取当前登录用户ID
            Long userId = SecurityUtils.getUserId();

            // 取消订单
            boolean result = alseOrderService.cancelOrder(orderId, userId);

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
    @PostMapping("/pay/{orderId}")
    @CheckToken
    @ApiOperation("支付订单")
    @Log(title = "支付订单", businessType = BusinessType.UPDATE)
    public AjaxResult payOrder(@PathVariable Long orderId, @RequestBody @Validated PayOrderRequestDTO requestDTO) {
        try {
            // 获取当前登录用户ID
            Long userId = SecurityUtils.getUserId();

            // 支付订单
            boolean result = alseOrderService.payOrder(orderId, userId, requestDTO);

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
    @PostMapping("/ship/{orderId}")
    @CheckToken
    @ApiOperation("发货")
    @Log(title = "订单发货", businessType = BusinessType.UPDATE)
    public AjaxResult shipOrder(@PathVariable Long orderId, @RequestBody @Validated ShipOrderRequestDTO requestDTO) {
        try {
            // 获取当前登录用户ID
            Long userId = SecurityUtils.getUserId();

            // 发货
            boolean result = alseOrderService.shipOrder(orderId, userId, requestDTO);

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
    @PostMapping("/confirm/{orderId}")
    @CheckToken
    @ApiOperation("确认收货")
    @Log(title = "确认收货", businessType = BusinessType.UPDATE)
    public AjaxResult confirmReceipt(@PathVariable Long orderId) {
        try {
            // 获取当前登录用户ID
            Long userId = SecurityUtils.getUserId();

            // 确认收货
            boolean result = alseOrderService.confirmReceipt(orderId, userId);

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


    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(new com.github.pagehelper.PageInfo(list).getTotal());
        return rspData;
    }
}

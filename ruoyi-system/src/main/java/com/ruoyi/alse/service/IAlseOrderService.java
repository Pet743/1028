package com.ruoyi.alse.service;

import java.util.List;
import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.uni.model.DTO.request.order.CreateOrderRequestDTO;
import com.ruoyi.uni.model.DTO.request.order.PayOrderRequestDTO;
import com.ruoyi.uni.model.DTO.request.order.ShipOrderRequestDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderDetailResponseDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderResponseDTO;

/**
 * 商品订单Service接口
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
public interface IAlseOrderService
{
    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否成功
     */
    public boolean cancelOrder(Long orderId, Long userId);

    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param requestDTO 支付请求
     * @return 是否成功
     */
    public boolean payOrder(Long orderId, Long userId, PayOrderRequestDTO requestDTO);

    /**
     * 发货
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param requestDTO 发货请求
     * @return 是否成功
     */
    public boolean shipOrder(Long orderId, Long userId, ShipOrderRequestDTO requestDTO);

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否成功
     */
    public boolean confirmReceipt(Long orderId, Long userId);


    /**
     * 创建订单
     *
     * @param requestDTO 创建订单请求
     * @param userId 用户ID
     * @return 订单响应DTO
     */
    public OrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO, Long userId);

    /**
     * 根据订单状态查询订单列表
     *
     * @param userId 用户ID
     * @param orderStatus 订单状态(0:全部)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    public List<OrderResponseDTO> getOrderListByStatus(Long userId, Integer orderStatus, Integer pageNum, Integer pageSize);

    /**
     * 查询订单详情
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 订单详情
     */
    public OrderDetailResponseDTO getOrderDetail(Long orderId, Long userId);

    /**
     * 查询商品订单
     *
     * @param orderId 商品订单主键
     * @return 商品订单
     */
    public AlseOrder selectAlseOrderByOrderId(Long orderId);

    /**
     * 查询商品订单列表
     *
     * @param alseOrder 商品订单
     * @return 商品订单集合
     */
    public List<AlseOrder> selectAlseOrderList(AlseOrder alseOrder);

    /**
     * 新增商品订单
     *
     * @param alseOrder 商品订单
     * @return 结果
     */
    public int insertAlseOrder(AlseOrder alseOrder);

    /**
     * 修改商品订单
     *
     * @param alseOrder 商品订单
     * @return 结果
     */
    public int updateAlseOrder(AlseOrder alseOrder);

    /**
     * 批量删除商品订单
     *
     * @param orderIds 需要删除的商品订单主键集合
     * @return 结果
     */
    public int deleteAlseOrderByOrderIds(Long[] orderIds);

    /**
     * 删除商品订单信息
     *
     * @param orderId 商品订单主键
     * @return 结果
     */
    public int deleteAlseOrderByOrderId(Long orderId);
}

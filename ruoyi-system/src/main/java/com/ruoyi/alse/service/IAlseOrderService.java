package com.ruoyi.alse.service;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.uni.model.DTO.request.order.CreateOrderRequestDTO;
import com.ruoyi.uni.model.DTO.request.order.PayOrderRequestDTO;
import com.ruoyi.uni.model.DTO.request.order.ShipOrderRequestDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderDetailResponseDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderResponseDTO;
import com.ruoyi.uni.model.DTO.respone.order.PaymentResultDTO;

/**
 * 商品订单Service接口
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
public interface IAlseOrderService
{

    /**
     * 统计用户卖出的商品总数
     *
     * @param userId 用户ID
     * @return 卖出商品总数
     */
    int countSoldProductsByUser(Long userId);

    /**
     * 统计用户购买的商品总数
     *
     * @param userId 用户ID
     * @return 购买商品总数
     */
    int countBoughtProductsByUser(Long userId);

    /**
     * 查询超时未支付订单
     *
     * @param timeoutMinutes 超时时间（分钟）
     * @return 超时订单列表
     */
    List<AlseOrder> getTimeoutUnpaidOrders(int timeoutMinutes);

    /**
     * 取消超时订单
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean cancelTimeoutOrder(Long orderId);

    /**
     * 查询最近一段时间内的待支付订单
     *
     * @param minutes 分钟数，例如30表示近30分钟内
     * @return 订单列表
     */
    public List<AlseOrder> getRecentOrders(int minutes);


    /**
     * 根据金额和订单号创建虚拟商品订单
     * 支付回调时，通过订单号和金额创建订单记录
     *
     * @param outTradeNo 订单号
     * @param totalAmount 支付金额
     * @return 创建的订单对象
     */
    public AlseOrder createVirtualOrder(String outTradeNo, BigDecimal totalAmount);

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
    public PaymentResultDTO createOrder(CreateOrderRequestDTO requestDTO, Long userId);

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
     * 重新发起支付
     */
    public PaymentResultDTO repayOrder(Long orderId, Long userId);

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

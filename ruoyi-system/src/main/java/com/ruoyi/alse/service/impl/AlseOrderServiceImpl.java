package com.ruoyi.alse.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import com.github.pagehelper.PageHelper;
import com.ruoyi.alse.domain.*;
import com.ruoyi.alse.service.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.uni.converter.OrderConverter;
import com.ruoyi.uni.model.DTO.request.order.CreateOrderRequestDTO;
import com.ruoyi.uni.model.DTO.request.order.PayOrderRequestDTO;
import com.ruoyi.uni.model.DTO.request.order.ShipOrderRequestDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderDetailResponseDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderResponseDTO;
import com.ruoyi.uni.model.Enum.OrderStatusEnum;
import com.ruoyi.uni.model.Enum.PaymentMethodEnum;
import com.ruoyi.uni.util.FinanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlseOrderMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品订单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-14
 */
@Service
@Slf4j
public class AlseOrderServiceImpl implements IAlseOrderService {

    @Autowired
    private AlseOrderMapper alseOrderMapper;

    @Autowired
    private IAlseUserService alseUserService;

    @Autowired
    private IAlseProductService alseProductService;

    @Autowired
    private IAlseUserAddressService alseUserAddressService;

    @Autowired
    private IAlseWalletTransactionService alseWalletTransactionService;

    /**
     * 查询最近一段时间内的待支付订单
     *
     * @param minutes 分钟数，例如30表示近30分钟
     * @return 订单列表
     */
    @Override
    public List<AlseOrder> getRecentOrders(int minutes) {
        log.info("查询最近{}分钟内的待支付订单", minutes);

        // 计算过去N分钟的时间点
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -minutes);
        Date startTime = calendar.getTime();

        // 构造查询条件
        AlseOrder queryParam = new AlseOrder();

        // 设置时间范围参数
        Map<String, Object> params = new HashMap<>();
        params.put("beginCreateTime", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", startTime));
        queryParam.setParams(params);
        // 执行查询获取所有订单
        List<AlseOrder> orderList = alseOrderMapper.selectAlseOrderList(queryParam);
        // 过滤只保留待付款和待发货的订单
        List<AlseOrder> filteredList = orderList.stream()
                .filter(order ->
                        order.getOrderStatus() == OrderStatusEnum.PENDING_PAYMENT.getCode() ||
                                order.getOrderStatus() == OrderStatusEnum.PENDING_SHIPMENT.getCode())
                .collect(Collectors.toList());
        // 按创建时间降序排序（最新的在前面）
        filteredList.sort(Comparator.comparing(AlseOrder::getCreateTime).reversed());
        log.info("查询到最近{}分钟内的相关订单（待付款和待发货）数量：{}", minutes, filteredList.size());
        return filteredList;
    }


    /**
     * 根据金额和订单号创建虚拟商品订单
     * 支付回调时，通过订单号和金额创建订单记录
     *
     * @param outTradeNo  订单号
     * @param totalAmount 支付金额
     * @return 创建的订单对象
     */
    @Transactional(rollbackFor = Exception.class)
    public AlseOrder createVirtualOrder(String outTradeNo, BigDecimal totalAmount) {
        log.info("开始创建虚拟订单，订单号：{}，支付金额：{}", outTradeNo, totalAmount);

        // 1. 创建订单对象
        AlseOrder order = new AlseOrder();
        order.setOrderNo(outTradeNo);

        // 2. 生成随机商品ID和名称
        Long productId = generateRandomProductId();
        String productName = generateRandomProductName();

        // 3. 设置商品信息
        order.setProductId(productId);
        order.setProductName(productName);
        // 使用一个可访问的商品图片URL
        order.setProductImageUrl("https://img.alicdn.com/bao/uploaded/i1/O1CN019LU4rQ1JtjU5nPPO8_!!6000000001086-0-yinhe.jpg");

        // 4. 设置价格信息 - 商品单价设置为接近总金额的整数
        BigDecimal productPrice = new BigDecimal(totalAmount.intValue());
        order.setProductPrice(productPrice);

        // 5. 设置购买数量和总金额
        // 数量设为1，总金额为传入的实际支付金额
        order.setQuantity(1L);
        order.setTotalAmount(totalAmount);

        // 6. 设置支付方式为支付宝(1)
        order.setPaymentMethod(1); // 支付宝

        // 7. 设置随机买家和卖家信息
        order.setBuyerId(generateRandomUserId());
        order.setBuyerName("买家_" + order.getBuyerId());
        order.setBuyerPhone("1888888" + (1000 + new java.util.Random().nextInt(9000)));

        order.setSellerId(generateRandomUserId());
        order.setSellerName("卖家_" + order.getSellerId());
        order.setSellerPhone("1999999" + (1000 + new java.util.Random().nextInt(9000)));

        // 8. 设置订单状态为已支付(待发货)
        order.setOrderStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());

        // 9. 设置随机店铺信息
        order.setShopName("商店_" + (100 + new java.util.Random().nextInt(900)));

        // 10. 设置收货地址
        order.setShippingAddress("北京市朝阳区XX路XX号");

        // 11. 设置支付时间
        order.setPaymentTime(DateUtils.getNowDate());

        // 12. 设置通用字段
        order.setStatus("0"); // 正常状态
        order.setCreateBy("system");
        order.setCreateTime(DateUtils.getNowDate());
        order.setUpdateBy("system");
        order.setUpdateTime(DateUtils.getNowDate());
        order.setRemark("下单时预付款订单");

        // 13. 保存订单
        alseOrderMapper.insertAlseOrder(order);

        log.info("虚拟订单创建成功，订单ID：{}", order.getOrderId());
        return order;
    }

    /**
     * 生成随机商品ID
     */
    private Long generateRandomProductId() {
        return 100000L + new java.util.Random().nextInt(900000);
    }

    /**
     * 生成随机用户ID
     */
    private Long generateRandomUserId() {
        return 10000L + new java.util.Random().nextInt(90000);
    }

    /**
     * 生成随机商品名称
     */
    private String generateRandomProductName() {
        String[] prefixes = {"高级", "豪华", "精品", "优质", "时尚"};
        String[] products = {"手机", "电脑", "耳机", "手表", "相机", "平板", "音箱"};
        String[] suffixes = {"Pro", "Max", "Ultra", "Plus", "Elite"};

        java.util.Random random = new java.util.Random();
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String product = products[random.nextInt(products.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];

        return prefix + product + " " + suffix;
    }


    /**
     * 取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId) {
        log.info("开始取消订单，订单ID：{}，用户ID：{}", orderId, userId);

        // 1. 查询订单
        AlseOrder order = alseOrderMapper.selectAlseOrderByOrderId(orderId);
        if (order == null) {
            log.error("取消订单失败：订单不存在，订单ID：{}", orderId);
            throw new ServiceException("订单不存在");
        }

        // 2. 验证权限
        if (!order.getBuyerId().equals(userId)) {
            log.error("取消订单失败：非订单所属用户，订单ID：{}，用户ID：{}", orderId, userId);
            throw new ServiceException("无权操作此订单");
        }

        // 3. 验证订单状态
        if (order.getOrderStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            log.error("取消订单失败：订单状态不允许取消，订单ID：{}，当前状态：{}", orderId, order.getOrderStatus());
            throw new ServiceException("当前订单状态不允许取消");
        }

        // 4. 更新订单状态
        order.setOrderStatus(OrderStatusEnum.CLOSED.getCode());
        order.setUpdateTime(DateUtils.getNowDate());
        order.setUpdateBy(String.valueOf(userId));

        // 5. 如果是钱包支付且已支付，需要退款处理
        if (order.getPaymentMethod() == PaymentMethodEnum.WALLET.getCode() &&
                order.getWalletTransactionId() != null &&
                order.getPaymentTime() != null) {
            processWalletRefund(order);
        }

        // 6. 更新订单
        int rows = alseOrderMapper.updateAlseOrder(order);
        return rows > 0;
    }

    /**
     * 支付订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, Long userId, PayOrderRequestDTO requestDTO) {
        log.info("开始支付订单，订单ID：{}，用户ID：{}，支付方式：{}", orderId, userId, requestDTO.getPaymentMethod());

        // 1. 查询订单
        AlseOrder order = alseOrderMapper.selectAlseOrderByOrderId(orderId);
        if (order == null) {
            log.error("支付订单失败：订单不存在，订单ID：{}", orderId);
            throw new ServiceException("订单不存在");
        }

        // 2. 验证权限
        if (!order.getBuyerId().equals(userId)) {
            log.error("支付订单失败：非订单所属用户，订单ID：{}，用户ID：{}", orderId, userId);
            throw new ServiceException("无权操作此订单");
        }

        // 3. 验证订单状态
        if (order.getOrderStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            log.error("支付订单失败：订单状态不允许支付，订单ID：{}，当前状态：{}", orderId, order.getOrderStatus());
            throw new ServiceException("当前订单状态不允许支付");
        }

        // 4. 更新订单信息
        order.setPaymentMethod(requestDTO.getPaymentMethod());
        order.setPaymentAccount(requestDTO.getPaymentAccount());
        order.setPaymentTime(DateUtils.getNowDate());
        order.setOrderStatus(OrderStatusEnum.PENDING_SHIPMENT.getCode());
        order.setUpdateTime(DateUtils.getNowDate());
        order.setUpdateBy(String.valueOf(userId));

        // 5. 如果是钱包支付，需要处理钱包交易
        if (requestDTO.getPaymentMethod() == PaymentMethodEnum.WALLET.getCode()) {
            // 查询用户信息
            AlseUser buyer = alseUserService.selectAlseUserByUserId(userId);
            if (buyer == null) {
                throw new ServiceException("用户不存在");
            }
            processWalletPayment(order, buyer);
        }

        // 6. 更新订单
        int rows = alseOrderMapper.updateAlseOrder(order);
        return rows > 0;
    }

    /**
     * 发货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shipOrder(Long orderId, Long userId, ShipOrderRequestDTO requestDTO) {
        log.info("开始发货，订单ID：{}，用户ID：{}，物流公司：{}，物流单号：{}",
                orderId, userId, requestDTO.getLogisticsCompany(), requestDTO.getLogisticsNo());

        // 1. 查询订单
        AlseOrder order = alseOrderMapper.selectAlseOrderByOrderId(orderId);
        if (order == null) {
            log.error("发货失败：订单不存在，订单ID：{}", orderId);
            throw new ServiceException("订单不存在");
        }

        // 2. 验证权限
        if (!order.getSellerId().equals(userId)) {
            log.error("发货失败：非订单卖家，订单ID：{}，用户ID：{}", orderId, userId);
            throw new ServiceException("只有卖家可以执行发货操作");
        }

        // 3. 验证订单状态
        if (order.getOrderStatus() != OrderStatusEnum.PENDING_SHIPMENT.getCode()) {
            log.error("发货失败：订单状态不允许发货，订单ID：{}，当前状态：{}", orderId, order.getOrderStatus());
            throw new ServiceException("当前订单状态不允许发货");
        }

        // 4. 更新订单信息
        order.setLogisticsCompany(requestDTO.getLogisticsCompany());
        order.setLogisticsNo(requestDTO.getLogisticsNo());
        order.setShippingTime(DateUtils.getNowDate());
        order.setOrderStatus(OrderStatusEnum.PENDING_RECEIPT.getCode());
        order.setUpdateTime(DateUtils.getNowDate());
        order.setUpdateBy(String.valueOf(userId));

        // 5. 更新订单
        int rows = alseOrderMapper.updateAlseOrder(order);
        return rows > 0;
    }

    /**
     * 确认收货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceipt(Long orderId, Long userId) {
        log.info("开始确认收货，订单ID：{}，用户ID：{}", orderId, userId);

        // 1. 查询订单
        AlseOrder order = alseOrderMapper.selectAlseOrderByOrderId(orderId);
        if (order == null) {
            log.error("确认收货失败：订单不存在，订单ID：{}", orderId);
            throw new ServiceException("订单不存在");
        }

        // 2. 验证权限
        if (!order.getBuyerId().equals(userId)) {
            log.error("确认收货失败：非订单所属用户，订单ID：{}，用户ID：{}", orderId, userId);
            throw new ServiceException("无权操作此订单");
        }

        // 3. 验证订单状态
        if (order.getOrderStatus() != OrderStatusEnum.PENDING_RECEIPT.getCode()) {
            log.error("确认收货失败：订单状态不允许确认收货，订单ID：{}，当前状态：{}", orderId, order.getOrderStatus());
            throw new ServiceException("当前订单状态不允许确认收货");
        }

        // 4. 更新订单信息
        order.setReceivingTime(DateUtils.getNowDate());
        order.setOrderStatus(OrderStatusEnum.PENDING_REVIEW.getCode());
        order.setUpdateTime(DateUtils.getNowDate());
        order.setUpdateBy(String.valueOf(userId));

        // 5. 处理卖家收款
        processSellerIncome(order);

        // 6. 更新订单
        int rows = alseOrderMapper.updateAlseOrder(order);

        // 7. 更新商品销售数量
        updateProductSalesCount(order.getProductId(), order.getQuantity());

        return rows > 0;
    }

    /**
     * 处理钱包退款
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    void processWalletRefund(AlseOrder order) {
        // 1. 查询原交易记录
        AlseWalletTransaction originalTransaction = alseWalletTransactionService.selectAlseWalletTransactionByTransactionId(
                order.getWalletTransactionId());

        if (originalTransaction == null) {
            log.error("处理钱包退款失败：原交易记录不存在，交易ID：{}", order.getWalletTransactionId());
            throw new ServiceException("原交易记录不存在，无法退款");
        }

        // 2. 查询买家信息
        AlseUser buyer = alseUserService.selectAlseUserByUserId(order.getBuyerId());
        if (buyer == null) {
            log.error("处理钱包退款失败：买家不存在，用户ID：{}", order.getBuyerId());
            throw new ServiceException("买家不存在，无法退款");
        }

        // 3. 创建退款交易记录
        AlseWalletTransaction refundTransaction = new AlseWalletTransaction();
        refundTransaction.setUserId(buyer.getUserId());
        refundTransaction.setUsername(buyer.getUsername());
        refundTransaction.setTransactionAmount(order.getTotalAmount());
        refundTransaction.setIncomeAmount(order.getTotalAmount());
        refundTransaction.setExpenseAmount(BigDecimal.ZERO);
        refundTransaction.setPaymentMethod(PaymentMethodEnum.WALLET.getCode());
        refundTransaction.setTransactionType(4); // 退款
        refundTransaction.setTransactionTime(DateUtils.getNowDate());
        refundTransaction.setStatus("0");
        refundTransaction.setCreateBy(buyer.getUsername());
        refundTransaction.setCreateTime(DateUtils.getNowDate());
        refundTransaction.setUpdateBy(buyer.getUsername());
        refundTransaction.setUpdateTime(DateUtils.getNowDate());
        refundTransaction.setRemark("订单取消退款：" + order.getOrderNo());

        // 4. 插入交易记录
        alseWalletTransactionService.insertAlseWalletTransaction(refundTransaction);

        // 5. 更新用户钱包余额（加回退款金额）
        buyer.setWalletBalance(FinanceUtils.add(buyer.getWalletBalance(), order.getTotalAmount()));
        buyer.setTotalExpense(FinanceUtils.subtract(buyer.getTotalExpense(), order.getTotalAmount()));
        buyer.setUpdateTime(DateUtils.getNowDate());
        buyer.setUpdateBy(buyer.getUsername());

        int result = alseUserService.updateAlseUser(buyer);
        if (result <= 0) {
            log.error("处理钱包退款失败：更新买家钱包余额失败，用户ID：{}", buyer.getUserId());
            throw new ServiceException("更新钱包余额失败，请稍后重试");
        }
    }

    /**
     * 处理卖家收入
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    void processSellerIncome(AlseOrder order) {
        // 1. 查询卖家信息
        AlseUser seller = alseUserService.selectAlseUserByUserId(order.getSellerId());
        if (seller == null) {
            log.error("处理卖家收入失败：卖家不存在，用户ID：{}", order.getSellerId());
            throw new ServiceException("卖家不存在");
        }

        // 2. 创建收入交易记录
        AlseWalletTransaction incomeTransaction = new AlseWalletTransaction();
        incomeTransaction.setUserId(seller.getUserId());
        incomeTransaction.setUsername(seller.getUsername());
        incomeTransaction.setTransactionAmount(order.getTotalAmount());
        incomeTransaction.setIncomeAmount(order.getTotalAmount());
        incomeTransaction.setExpenseAmount(BigDecimal.ZERO);
        incomeTransaction.setPaymentMethod(order.getPaymentMethod());
        incomeTransaction.setTransactionType(2); // 售出商品
        incomeTransaction.setTransactionTime(DateUtils.getNowDate());
        incomeTransaction.setStatus("0");
        incomeTransaction.setCreateBy(seller.getUsername());
        incomeTransaction.setCreateTime(DateUtils.getNowDate());
        incomeTransaction.setUpdateBy(seller.getUsername());
        incomeTransaction.setUpdateTime(DateUtils.getNowDate());
        incomeTransaction.setRemark("订单收入：" + order.getOrderNo());

        // 3. 插入交易记录
        alseWalletTransactionService.insertAlseWalletTransaction(incomeTransaction);

        // 4. 更新卖家钱包余额和总收入
        seller.setWalletBalance(FinanceUtils.add(seller.getWalletBalance(), order.getTotalAmount()));
        seller.setTotalIncome(FinanceUtils.add(seller.getTotalIncome(), order.getTotalAmount()));
        seller.setUpdateTime(DateUtils.getNowDate());
        seller.setUpdateBy(seller.getUsername());

        int result = alseUserService.updateAlseUser(seller);
        if (result <= 0) {
            log.error("处理卖家收入失败：更新卖家钱包余额失败，用户ID：{}", seller.getUserId());
            throw new ServiceException("更新卖家钱包余额失败");
        }
    }

    /**
     * 更新商品销售数量
     */
    private void updateProductSalesCount(Long productId, Long quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            return;
        }
        try {
            AlseProduct product = alseProductService.selectAlseProductByProductId(productId);
            if (product != null) {
                product.setSalesCount(product.getSalesCount() + quantity.intValue());
                alseProductService.updateAlseProduct(product);
            }
        } catch (Exception e) {
            log.error("更新商品销售数量失败，商品ID：{}，数量：{}", productId, quantity, e);
        }
    }

    /**
     * 创建订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO, Long userId) {
        log.info("开始创建订单，用户ID：{}，请求参数：{}", userId, requestDTO);

        // 1. 异步获取必要信息
        CompletableFuture<AlseUser> userFuture = CompletableFuture.supplyAsync(() ->
                alseUserService.selectAlseUserByUserId(userId)
        );

        CompletableFuture<AlseProduct> productFuture = CompletableFuture.supplyAsync(() ->
                alseProductService.selectAlseProductByProductId(requestDTO.getProductId())
        );

        CompletableFuture<AlseUserAddress> addressFuture = CompletableFuture.supplyAsync(() ->
                alseUserAddressService.selectAlseUserAddressByAddressId(requestDTO.getShippingAddressId())
        );

        // 2. 等待所有异步任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(userFuture, productFuture, addressFuture);

        try {
            allFutures.get();

            // 3. 获取异步任务结果
            AlseUser buyer = userFuture.get();
            AlseProduct product = productFuture.get();
            AlseUserAddress address = addressFuture.get();

            if (buyer == null) {
                throw new ServiceException("用户不存在");
            }
            if (product == null) {
                throw new ServiceException("商品不存在");
            }
            if (address == null || !address.getUserId().equals(userId)) {
                throw new ServiceException("收货地址不存在或不属于当前用户");
            }

            // 5. 计算订单金额（使用 FinanceUtils.calculateTotal）
            BigDecimal totalAmount = FinanceUtils.calculateTotal(product.getProductPrice(), requestDTO.getQuantity());

            // 6. 创建订单对象
            AlseOrder order = new AlseOrder();
            order.setOrderNo(generateOrderNo());
            order.setProductId(product.getProductId());
            order.setProductName(product.getProductTitle());
            order.setProductImageUrl(product.getProductCoverImg());
            order.setProductPrice(product.getProductPrice());
            order.setQuantity(requestDTO.getQuantity());
            order.setTotalAmount(totalAmount);
            order.setPaymentMethod(requestDTO.getPaymentMethod());
            order.setPaymentAccount(requestDTO.getPaymentAccount());

            // 接收付款方式默认与商品发布者的收款方式一致
            AlseUser seller = alseUserService.selectAlseUserByUserId(product.getPublisherId());
            if (seller != null) {
                order.setReceivingMethod(seller.getPaymentMethod());
                order.setReceivingAccount(seller.getPaymentAccount());
            }

            // 买家信息
            order.setBuyerId(buyer.getUserId());
            order.setBuyerPhone(buyer.getPhone());
            order.setBuyerName(buyer.getNickname() != null ? buyer.getNickname() : buyer.getUsername());

            // 卖家信息
            order.setSellerId(product.getPublisherId());
            order.setSellerPhone(seller != null ? seller.getPhone() : null);
            order.setSellerName(seller != null ? (seller.getNickname() != null ? seller.getNickname() : seller.getUsername()) : null);

            // 店铺信息
            order.setShopId(null); // 暂不设置
            order.setShopName(product.getShopName());

            // 收货地址
            order.setShippingAddressId(address.getAddressId());
            String fullAddress = address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress();
            order.setShippingAddress(fullAddress);

            // 订单状态 - 默认待付款
            order.setOrderStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());

            // 通用字段
            order.setStatus("0");
            order.setCreateBy(buyer.getUsername());
            order.setCreateTime(DateUtils.getNowDate());
            order.setUpdateBy(buyer.getUsername());
            order.setUpdateTime(DateUtils.getNowDate());
            order.setRemark(requestDTO.getRemark());

            // 7. 处理钱包支付
            if (requestDTO.getPaymentMethod() == PaymentMethodEnum.WALLET.getCode()) {
                processWalletPayment(order, buyer);
            }

            // 8. 插入订单
            alseOrderMapper.insertAlseOrder(order);

            // 9. 返回结果
            return OrderConverter.convertToOrderResponseDTO(order);

        } catch (InterruptedException | ExecutionException e) {
            log.error("创建订单异步获取数据异常", e);
            Thread.currentThread().interrupt();
            throw new ServiceException("创建订单失败，请稍后重试");
        }
    }

    /**
     * 处理钱包支付
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    void processWalletPayment(AlseOrder order, AlseUser buyer) {
        // 1. 检查余额是否充足
        if (buyer.getWalletBalance().compareTo(order.getTotalAmount()) < 0) {
            throw new ServiceException("钱包余额不足，请充值后再支付");
        }

        // 2. 创建钱包交易记录
        AlseWalletTransaction transaction = new AlseWalletTransaction();
        transaction.setUserId(buyer.getUserId());
        transaction.setUsername(buyer.getUsername());
        transaction.setTransactionAmount(order.getTotalAmount());
        transaction.setExpenseAmount(order.getTotalAmount());
        transaction.setIncomeAmount(BigDecimal.ZERO);
        transaction.setPaymentMethod(PaymentMethodEnum.WALLET.getCode());
        transaction.setTransactionType(1); // 购买商品
        transaction.setTransactionTime(DateUtils.getNowDate());
        transaction.setStatus("0");
        transaction.setCreateBy(buyer.getUsername());
        transaction.setCreateTime(DateUtils.getNowDate());
        transaction.setUpdateBy(buyer.getUsername());
        transaction.setUpdateTime(DateUtils.getNowDate());
        transaction.setRemark("订单支付：" + order.getOrderNo());

        // 3. 插入交易记录
        alseWalletTransactionService.insertAlseWalletTransaction(transaction);

        // 4. 更新订单交易ID和支付时间
        order.setWalletTransactionId(transaction.getTransactionId());
        order.setPaymentTime(DateUtils.getNowDate());
        order.setOrderStatus(OrderStatusEnum.PENDING_SHIPMENT.getCode());

        // 5. 更新用户钱包余额（扣除支付金额）
        buyer.setWalletBalance(FinanceUtils.subtract(buyer.getWalletBalance(), order.getTotalAmount()));
        buyer.setTotalExpense(FinanceUtils.add(buyer.getTotalExpense(), order.getTotalAmount()));
        buyer.setUpdateTime(DateUtils.getNowDate());
        buyer.setUpdateBy(buyer.getUsername());

        int result = alseUserService.updateAlseUser(buyer);
        if (result <= 0) {
            throw new ServiceException("更新钱包余额失败，请稍后重试");
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf(1000 + (int) (Math.random() * 9000));
        return timestamp + random;
    }

    /**
     * 根据订单状态查询订单列表
     */
    @Override
    public List<OrderResponseDTO> getOrderListByStatus(Long userId, Integer orderStatus, Integer pageNum, Integer pageSize) {
        if (orderStatus == null || orderStatus < 0) {
            orderStatus = OrderStatusEnum.ALL.getCode();
        }

        AlseOrder queryParam = new AlseOrder();
        queryParam.setBuyerId(userId);

        if (orderStatus != OrderStatusEnum.ALL.getCode()) {
            queryParam.setOrderStatus(orderStatus);
        }

        PageHelper.startPage(pageNum, pageSize);
        List<AlseOrder> orderList = alseOrderMapper.selectAlseOrderList(queryParam);
        return OrderConverter.convertToOrderResponseDTOList(orderList);
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderDetailResponseDTO getOrderDetail(Long orderId, Long userId) {
        AlseOrder order = alseOrderMapper.selectAlseOrderByOrderId(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new ServiceException("无权查看该订单");
        }
        return OrderConverter.convertToOrderDetailResponseDTO(order);
    }

    /**
     * 查询商品订单
     *
     * @param orderId 商品订单主键
     * @return 商品订单
     */
    @Override
    public AlseOrder selectAlseOrderByOrderId(Long orderId) {
        return alseOrderMapper.selectAlseOrderByOrderId(orderId);
    }

    /**
     * 查询商品订单列表
     *
     * @param alseOrder 商品订单
     * @return 商品订单
     */
    @Override
    public List<AlseOrder> selectAlseOrderList(AlseOrder alseOrder) {
        return alseOrderMapper.selectAlseOrderList(alseOrder);
    }

    /**
     * 新增商品订单
     *
     * @param alseOrder 商品订单
     * @return 结果
     */
    @Override
    public int insertAlseOrder(AlseOrder alseOrder) {
        alseOrder.setCreateTime(DateUtils.getNowDate());
        return alseOrderMapper.insertAlseOrder(alseOrder);
    }

    /**
     * 修改商品订单
     *
     * @param alseOrder 商品订单
     * @return 结果
     */
    @Override
    public int updateAlseOrder(AlseOrder alseOrder) {
        alseOrder.setUpdateTime(DateUtils.getNowDate());
        return alseOrderMapper.updateAlseOrder(alseOrder);
    }

    /**
     * 批量删除商品订单
     *
     * @param orderIds 需要删除的商品订单主键
     * @return 结果
     */
    @Override
    public int deleteAlseOrderByOrderIds(Long[] orderIds) {
        return alseOrderMapper.deleteAlseOrderByOrderIds(orderIds);
    }

    /**
     * 删除商品订单信息
     *
     * @param orderId 商品订单主键
     * @return 结果
     */
    @Override
    public int deleteAlseOrderByOrderId(Long orderId) {
        return alseOrderMapper.deleteAlseOrderByOrderId(orderId);
    }
}

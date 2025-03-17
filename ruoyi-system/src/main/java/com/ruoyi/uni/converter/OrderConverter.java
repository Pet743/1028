package com.ruoyi.uni.converter;


import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.uni.model.DTO.respone.order.OrderDetailResponseDTO;
import com.ruoyi.uni.model.DTO.respone.order.OrderResponseDTO;
import com.ruoyi.uni.model.Enum.OrderStatusEnum;
import com.ruoyi.uni.model.Enum.PaymentMethodEnum;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单数据转换器
 */
public class OrderConverter {

    /**
     * 订单实体转订单响应DTO
     */
    public static OrderResponseDTO convertToOrderResponseDTO(AlseOrder order) {
        if (order == null) {
            return null;
        }

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderNo(order.getOrderNo());
        dto.setProductId(order.getProductId());
        dto.setProductName(order.getProductName());
        dto.setProductImageUrl(order.getProductImageUrl());
        dto.setProductPrice(order.getProductPrice());
        dto.setQuantity(order.getQuantity());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentMethodDesc(PaymentMethodEnum.getByCode(order.getPaymentMethod()).getDesc());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setOrderStatusDesc(OrderStatusEnum.getByCode(order.getOrderStatus()).getDesc());
        dto.setBuyerId(order.getBuyerId());
        dto.setBuyerName(order.getBuyerName());
        dto.setBuyerPhone(order.getBuyerPhone());
        dto.setSellerId(order.getSellerId());
        dto.setSellerName(order.getSellerName());
        dto.setSellerPhone(order.getSellerPhone());
        dto.setShopName(order.getShopName());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setLogisticsCompany(order.getLogisticsCompany());
        dto.setLogisticsNo(order.getLogisticsNo());
        dto.setCreateTime(order.getCreateTime());
        dto.setPaymentTime(order.getPaymentTime());
        dto.setShippingTime(order.getShippingTime());
        dto.setReceivingTime(order.getReceivingTime());

        return dto;
    }

    /**
     * 订单实体列表转订单响应DTO列表
     */
    public static List<OrderResponseDTO> convertToOrderResponseDTOList(List<AlseOrder> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return Collections.emptyList();
        }

        return orderList.stream()
                .map(OrderConverter::convertToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 订单实体转订单详情响应DTO
     */
    public static OrderDetailResponseDTO convertToOrderDetailResponseDTO(AlseOrder order) {
        if (order == null) {
            return null;
        }

        OrderDetailResponseDTO detailDTO = new OrderDetailResponseDTO();
        // 复制基本属性
        OrderResponseDTO baseDTO = convertToOrderResponseDTO(order);
        // 使用BeanUtils复制属性
        org.springframework.beans.BeanUtils.copyProperties(baseDTO, detailDTO);

        // 设置额外的详情属性
        detailDTO.setWalletTransactionId(order.getWalletTransactionId());
        detailDTO.setRemark(order.getRemark());

        return detailDTO;
    }
}
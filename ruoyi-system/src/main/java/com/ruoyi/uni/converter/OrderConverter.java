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

        // 设置物流信息
        detailDTO.setLogisticsCompany(order.getLogisticsCompany());
        detailDTO.setLogisticsNo(order.getLogisticsNo());

        // 设置时间信息
        detailDTO.setPaymentTime(order.getPaymentTime());
        detailDTO.setShippingTime(order.getShippingTime());
        detailDTO.setReceivingTime(order.getReceivingTime());

        // 解析收货地址信息
        parseShippingAddressInfo(order, detailDTO);

        return detailDTO;
    }

    /**
     * 解析订单收货地址信息
     */
    private static void parseShippingAddressInfo(AlseOrder order, OrderDetailResponseDTO detailDTO) {
        if (order.getShippingAddressId() != null) {
            // 此处可以根据实际需求决定是调用地址服务查询完整地址，还是解析shippingAddress字段
            // 方案1: 解析已有的收货地址字符串(简单实现)
            String fullAddress = order.getShippingAddress();
            if (fullAddress != null && !fullAddress.isEmpty()) {
                // 假设shippingAddress格式为: 省份城市区县详细地址
                // 可以根据实际格式进行调整解析逻辑
                try {
                    // 简单解析逻辑，实际项目中可能需要更复杂的解析或直接查询地址表
                    int provinceEnd = Math.min(fullAddress.length(), 3);  // 省份通常2-3个字
                    int cityEnd = Math.min(fullAddress.length(), provinceEnd + 3);  // 城市通常2-4个字
                    int districtEnd = Math.min(fullAddress.length(), cityEnd + 4);  // 区县通常2-4个字

                    detailDTO.setProvince(fullAddress.substring(0, provinceEnd));
                    detailDTO.setCity(fullAddress.substring(provinceEnd, cityEnd));
                    detailDTO.setDistrict(fullAddress.substring(cityEnd, districtEnd));
                    detailDTO.setDetailAddress(fullAddress.substring(districtEnd));

                    // 设置联系人信息
                    detailDTO.setContactName(order.getBuyerName());
                    detailDTO.setContactPhone(order.getBuyerPhone());
                } catch (Exception e) {
                    // 解析失败时的处理
                    detailDTO.setDetailAddress(fullAddress);
                }
            }
        }
    }
}
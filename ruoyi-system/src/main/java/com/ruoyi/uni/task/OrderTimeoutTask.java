package com.ruoyi.uni.task;

import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.service.IAlseOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;


/**
 * 订单超时处理任务
 */
@Component("orderTimeoutTask")
@Slf4j
public class OrderTimeoutTask {
    @Autowired
    private IAlseOrderService orderService;

    /**
     * 处理超时未支付订单
     * 每5分钟执行一次
     */
    public void handleTimeoutOrders() {
        log.info("开始处理超时未支付订单");

        try {
            // 查询超过30分钟未支付的订单
            List<AlseOrder> timeoutOrders = orderService.getTimeoutUnpaidOrders(30);

            if (timeoutOrders.isEmpty()) {
                log.info("没有超时未支付订单需要处理");
                return;
            }

            log.info("找到{}个超时未支付订单", timeoutOrders.size());

            // 处理每个超时订单
            for (AlseOrder order : timeoutOrders) {
                try {
                    // 取消订单并恢复库存
                    orderService.cancelTimeoutOrder(order.getOrderId());
                    log.info("成功取消超时订单：{}", order.getOrderId());
                } catch (Exception e) {
                    log.error("取消超时订单失败，订单ID：{}", order.getOrderId(), e);
                }
            }

            log.info("超时未支付订单处理完成");
        } catch (Exception e) {
            log.error("处理超时未支付订单异常", e);
        }
    }
}

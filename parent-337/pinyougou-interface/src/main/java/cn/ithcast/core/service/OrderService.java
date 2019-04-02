package cn.ithcast.core.service;

import cn.itcast.core.pojo.order.Order;

import java.util.List;

public interface OrderService {

    /**
     * 保存到订单
     * @param order
     */
    void add(Order order);

}

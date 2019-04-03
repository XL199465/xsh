package cn.ithcast.core.service;

import cn.itcast.core.pojo.order.Order;

import java.util.List;

public interface OrderService {

    /**
     * 保存到订单
     * @param order
     */
    void add(Order order);

    //查询所有订单
    List<Order> findAllOrders(String name);

    //订单发货
    void ordersShipment(String[] ids);
}

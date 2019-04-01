package cn.ithcast.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;

import java.util.List;

public interface OrderService {

    /**
     * 保存到订单
     * @param order
     */
    void add(Order order);

    //查询所有订单
    List<OrderItem> findAllOrders( String username);
}

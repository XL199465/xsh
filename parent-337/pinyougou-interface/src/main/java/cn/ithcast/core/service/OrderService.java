package cn.ithcast.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojogroup.Orderpp;

import java.util.List;

public interface OrderService {

    /**
     * 保存到订单
     * @param order
     */
    void add(Order order);



    //商家后台 查询订单
    List<Order> findAllOrder(String name);
}

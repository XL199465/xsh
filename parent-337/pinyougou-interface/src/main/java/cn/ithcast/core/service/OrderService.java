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

    //查询我的订单
    List<Orderpp> findAllOrders(String name);
}

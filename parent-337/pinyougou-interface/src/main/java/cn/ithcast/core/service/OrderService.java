package cn.ithcast.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojogroup.Orderpp;
import entity.PageResult;

import java.util.List;

public interface OrderService {

    /**
     * 保存到订单
     * @param order
     */
    void add(Order order);

    //查询我的订单
    List<Orderpp> findAllOrders(String name);

    //商家后台 查询订单
   PageResult findAllOrder(String name , Integer num , Integer size , Integer a);

    //订单发货
    void ordersShipment(String[] ids);

   /* //订单统计
    List<Order> ordersStatistics(Integer a);*/
}

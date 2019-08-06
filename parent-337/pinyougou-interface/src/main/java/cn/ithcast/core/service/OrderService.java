package cn.ithcast.core.service;

import cn.ithcast.core.pojo.order.Order;
import entity.PageResult;

public interface OrderService {

    /**
     * 保存到订单
     * @param order
     */
    void add(Order order);



    //商家后台 查询订单
   PageResult findAllOrder(String name , Integer num , Integer size , Integer a);

    //订单发货
    void ordersShipment(String[] ids);

   /* //订单统计
    List<Order> ordersStatistics(Integer a);*/
}

package cn.ithcast.core.pojogroup;

import cn.ithcast.core.pojo.order.Order;
import cn.ithcast.core.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

public class Orderpp implements Serializable {

    private Order order;
    private List<OrderItem> orderitemList;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderitemList() {
        return orderitemList;
    }

    public void setOrderitemList(List<OrderItem> orderitemList) {
        this.orderitemList = orderitemList;
    }
}

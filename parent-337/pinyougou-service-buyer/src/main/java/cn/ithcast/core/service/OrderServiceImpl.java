package cn.ithcast.core.service;

import cn.ithcast.core.common.utils.IdWorker;
import cn.ithcast.core.dao.log.PayLogDao;
import cn.ithcast.core.dao.order.OrderDao;
import cn.ithcast.core.dao.order.OrderItemDao;
import cn.ithcast.core.pojo.item.Item;
import cn.ithcast.core.pojo.log.PayLog;
import cn.ithcast.core.pojo.log.PayLogQuery;
import cn.ithcast.core.pojo.order.Order;
import cn.ithcast.core.pojo.order.OrderItem;
import cn.ithcast.core.pojo.order.OrderItemQuery;
import cn.ithcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.Cart;
import cn.ithcast.core.pojogroup.Orderpp;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@SuppressWarnings("all")
public class OrderServiceImpl implements OrderService {

    // 注入OrderDao对象
    @Autowired
    private OrderDao orderDao;

    // 注入OrderItemDao对象
    @Autowired
    private OrderItemDao orderItemDao;

    // 注入CartService对象
    @Autowired
    private CartService cartService;

    // 注入缓存对象
    @Autowired
    private RedisTemplate redisTemplate;

    // 注入IdWorker对象
    @Autowired
    private IdWorker idWorker;

    // 注入PayLogDao对象
    @Autowired
    private PayLogDao payLogDao;

    @Override
    public void add(Order order) {
        // 从缓存中获取到购物车集合
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(order.getUserId());

        // 获取完整的购物车集合
        List<Cart> cartListWithData = cartService.findCartList(cartList);

        // 初始化支付总金额为0
        Long totalFee = 0L;

        // 初始化订单编号集合
        List<Long> ids = new ArrayList<>();

        // 遍历完整的购物车集合,并转化为订单保存到订单表中
        for (Cart cart : cartListWithData) {
            // 设置订单编号
            order.setOrderId(idWorker.nextId());
            // 将订单编号存入订单编号集合中
            ids.add(order.getOrderId());
            // 设置邮费
            order.setPostFee("0");
            // 设置订单状态 未付款
            order.setStatus("1");
            // 设置创建和更新时间
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            // 设置订单来源
            order.setSourceType("2");
            // 设置商家id
            order.setSellerId(cart.getSellerId());

            // 设置支付类型,前端已经选择了
            // 设置用户名,controller层已经设置
            // 设置收货地址,收货人手机号,收货人姓名,前端都已设置完毕

            // 初始化payment为0
            double payment = 0D;

            // 保存订单详情
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                // 获取每个购物项对应的SKU库存对象
                Item item = cartService.findItemByItemId(orderItem.getItemId());
                // 设置订单详情编号
                orderItem.setId(idWorker.nextId());
                // 设置商品编号
                orderItem.setGoodsId(item.getGoodsId());
                // 设置订单编号
                orderItem.setOrderId(order.getOrderId());
                // 设置订单详情标题
                orderItem.setTitle(item.getTitle());
                // 设置订单详情价格
                orderItem.setPrice(item.getPrice());
                // 设置小计
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                // 再将小计取出求和,赋值给订单表中的实际支付
                payment += orderItem.getTotalFee().doubleValue();

                // 设置图片
                orderItem.setPicPath(item.getImage());
                // 设置商家id
                orderItem.setSellerId(item.getSellerId());
                // 保存订单详情
                orderItemDao.insertSelective(orderItem);
            }

            //TODO 设置实付金额
            order.setPayment(new BigDecimal(payment));
            // 将每个订单的金额放入支付总金额中
            totalFee += order.getPayment().longValue() * 100;

            // 保存订单
            orderDao.insertSelective(order);
        }

        // 判断如果是微信支付
        if ("1".equals(order.getPaymentType())) {
            PayLog payLog = new PayLog();
            // 设置支付日志交易标号
            payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
            // 设置创建时间
            payLog.setCreateTime(new Date());
            // 设置总金额
            payLog.setTotalFee(totalFee);
            // 设置用户名
            payLog.setUserId(order.getUserId());
            // 设置交易状态
            payLog.setTradeState("0");
            // 设置订单集合
            String orderList = ids.toString().replace("[", "").replace("]", "");
            payLog.setOrderList(orderList);
            // 设置支付方式
            payLog.setPayType("1");
            // 添加到支付日志表中
            payLogDao.insertSelective(payLog);
            // 存入缓存
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
        }

        // 最后清空购物车
        redisTemplate.boundHashOps("CART").delete(order.getUserId());
    }

    //查询我的订单
    @Override
    public List<Orderpp> findAllOrders(String name) {
        List<Orderpp> orderppList = new ArrayList<>();

        //根据用户名查询订单
        PayLogQuery query = new PayLogQuery();
        query.createCriteria().andUserIdEqualTo(name);
        //获取该用户多个订单
        List<PayLog> logList = payLogDao.selectByExample(query);

        for (PayLog payLog : logList) {
            String orderList = payLog.getOrderList();
            String[] orderList_Order_Id = orderList.split(",");
            //根据订单号查询order表
            for (String s : orderList_Order_Id) {
                Orderpp orderpp = new Orderpp();
                Order order = new Order();

                order = orderDao.selectByPrimaryKey(Long.parseLong(s.trim()));
                orderpp.setOrder(order);
                //根据order表order-id查询商品结果集
                OrderItemQuery orderItemQuery = new OrderItemQuery();
                orderItemQuery.createCriteria().andOrderIdEqualTo(order.getOrderId());
                List<OrderItem> orderItemList1 = orderItemDao.selectByExample(orderItemQuery);
                List<OrderItem> orderItemList = new ArrayList<>();

                for (OrderItem item : orderItemList1) {
                    orderItemList.add(item);
                }

                orderpp.setOrderitemList(orderItemList);
                orderppList.add(orderpp);
            }
        }
        return orderppList;
    }

    //商家后台查询订单
    @Override
    public PageResult findAllOrder(String name , Integer num , Integer size , Integer a) {
        PageHelper.startPage(num, size);

        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        criteria.andSellerIdEqualTo(name);
        criteria.andPaymentTimeIsNotNull();
        List<Order> orderList = orderDao.selectByExample(orderQuery);

        if (a == 0 ){
            Page<Order> page = (Page<Order>) orderList;
            return new PageResult(page.getTotal(), page.getResult());
        }

        Page<Order> orders = new Page<>();
        if (a == 1) {
            for (Order order : orderList) {
                Date paymentTime = order.getPaymentTime();
                long orderTime = paymentTime.getTime();
                long newTime = System.currentTimeMillis();

                if (orderTime > (newTime - 7 * 24 * 3600 * 1000)) {
                    orders.add(order);
                }
            }
            return new PageResult(orders.getTotal(), orders.getResult());

        }else if (a == 2){
            for (Order order : orderList) {
                Date paymentTime = order.getPaymentTime();
                long orderTime = paymentTime.getTime();
                long newTime = System.currentTimeMillis();
                if ((newTime - 30 * 24 *3600 * 1000) > orderTime &&  orderTime < (newTime - 7 * 24 * 3600 * 1000)) {
                    orders.add(order);
                }
            }
            return new PageResult(orders.getTotal(), orders.getResult());

        }
            for (Order order : orderList) {
                Date paymentTime = order.getPaymentTime();
                long orderTime = paymentTime.getTime();
                long newTime = System.currentTimeMillis();
                if ((newTime - 365 * 24 * 3600 * 1000) > orderTime &&  orderTime < (newTime - 30 * 24 *3600 * 1000)) {
                    orders.add(order);
                }
            }

        return new PageResult(orders.getTotal(), orders.getResult());
    }

    //订单发货
    @Override
    public void ordersShipment(String[] ids) {
        Order order = new Order();
        order.setStatus("2");

        for (String id : ids) {
            order.setOrderId(Long.valueOf(id));
            orderDao.updateByPrimaryKeySelective(order);
        }

    }

    /*//订单统计
    @Override
    public List<Order> ordersStatistics(Integer a) {
        OrderQuery orderQuery = new OrderQuery();
        orderQuery.createCriteria().andPaymentTimeIsNotNull();
        List<Order> orderList = orderDao.selectByExample(orderQuery);
        ArrayList<Order> orders = new ArrayList<>();
        if (a == 1) {
            for (Order order : orderList) {
                Date paymentTime = order.getPaymentTime();
                long orderTime = paymentTime.getTime();
                long newTime = System.currentTimeMillis();
                if (newTime - orderTime < 7 * 24 * 3600 * 1000) {
                    orders.add(order);
                }
            }
        }else if (a == 2){
            for (Order order : orderList) {
                Date paymentTime = order.getPaymentTime();
                long orderTime = paymentTime.getTime();
                long newTime = System.currentTimeMillis();
                if (30 * 24 *3600 * 1000 > newTime - orderTime &&  newTime - orderTime > 7 * 24 * 3600 * 1000) {
                    orders.add(order);
                }
            }
        }else {
            for (Order order : orderList) {
                Date paymentTime = order.getPaymentTime();
                long orderTime = paymentTime.getTime();
                long newTime = System.currentTimeMillis();
                if ( 365 * 24 * 3600 * 1000> newTime - orderTime &&  newTime - orderTime > 30 * 24 *3600 * 1000) {
                    orders.add(order);
                }
            }
        }


        return orders;
    }*/

}

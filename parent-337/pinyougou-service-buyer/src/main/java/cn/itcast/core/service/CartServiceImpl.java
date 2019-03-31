package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.ithcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Service;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {


    // 注入ItemDao对象
    @Autowired
    private ItemDao itemDao;

    // 注入RedisTemplate对象
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据库存id查询库存对象
     *
     * @param itemId
     * @return
     */
    @Override
    public Item findItemByItemId(Long itemId) {
        return itemDao.selectByPrimaryKey(itemId);
    }


    /**
     * 封装数据
     *
     * @param cartList
     */
    @Override
    public List<Cart> findCartList(List<Cart> cartList) {
        for (Cart cart : cartList) {
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                // 获取SKU库存对象
                Item item = findItemByItemId(orderItem.getItemId());
                // 商家名称
                cart.setSellerName(item.getSeller());
                // 图片
                orderItem.setPicPath(item.getImage());
                // 标题
                orderItem.setTitle(item.getTitle());
                // 价格
                orderItem.setPrice(item.getPrice());
                // 小计
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum()));
            }
        }

        return cartList;
    }

    /**
     * 添加整合后的购物车集合到缓存中
     *
     * @param cookieCartList
     */
    @Override
    public void addCartListToRedis(List<Cart> cookieCartList, String name) {
        // 从缓存中获取购物车集合
        List<Cart> redisCartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(name);

        // 将整合后的购物车集合存到缓存的购物车集合中
        redisCartList = mergeCartList(cookieCartList, redisCartList);


        // 将最终的购物车集合放到缓存中
        redisTemplate.boundHashOps("CART").put(name, redisCartList);
    }

    private List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        // 首先判断cookie中的购物车集合是否为空
        if (null != cookieCartList && cookieCartList.size() > 0) {
            // 判断缓存中的购物车集合是否为空
            if (null != redisCartList && redisCartList.size() > 0) {
                // 不为空
                // 遍历集合 并将cookie中的购物车集合合并到缓存中的购物车集合
                for (Cart cart : cookieCartList) {
                    int cartIndex = redisCartList.indexOf(cart);
                    if (cartIndex != -1) {
                        Cart redisCart = redisCartList.get(cartIndex);
                        List<OrderItem> redisOrderItemList = redisCart.getOrderItemList();
                        List<OrderItem> orderItemList = cart.getOrderItemList();
                        for (OrderItem orderItem : orderItemList) {
                            int orderItemIndex = redisOrderItemList.indexOf(orderItem);
                            if (orderItemIndex != -1) {
                                // 更改数量
                                OrderItem redisOrderItem = redisOrderItemList.get(orderItemIndex);
                                redisOrderItem.setNum(redisOrderItem.getNum() + orderItem.getNum());
                            } else {
                                redisOrderItemList.add(orderItem);
                            }
                        }
                    } else {
                        redisCartList.add(cart);
                    }
                }
            } else {
                // 返回cookie中的购物车集合,返回空
                return cookieCartList;
            }
        }
        return redisCartList;
    }

    /**
     * 从缓存中获取购物车集合
     *
     * @param name
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String name) {
        return (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
    }
}

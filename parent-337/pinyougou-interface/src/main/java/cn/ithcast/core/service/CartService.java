package cn.ithcast.core.service;

import cn.itcast.core.pojo.item.Item;
import entity.Cart;

import java.util.List;

public interface CartService {

    /**
     * 根据库存id查询库存对象
     *
     * @param itemId
     * @return
     */
    Item findItemByItemId(Long itemId);


    /**
     * 封装数据
     *
     * @param cartList
     */
    List<Cart> findCartList(List<Cart> cartList);

    /**
     * 添加整合后的购物车集合到缓存中
     * @param cookieCartList
     */
    void addCartListToRedis(List<Cart> cookieCartList, String name);

    /**
     * 从缓存中获取购物车集合
     * @param name
     * @return
     */
    List<Cart> findCartListFromRedis(String name);
}

package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import entity.Cart;
import entity.Collect;

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
     * 封装数据
     *
     * @param collectList
     */
    List<Collect> findCollectLists(List<Collect> collectList);

    /**
     * 添加整合后的购物车集合到缓存中
     * @param cookieCartList
     */
    void addCartListToRedis(List<Cart> cookieCartList, String name);
    /**
     * 添加整合后的收藏集合到缓存中
     * @param cookieCollectList
     */
    void addCollectListToRedi(List<Collect> cookieCollectList, String name);

    /**
     * 从缓存中获取购物车集合
     * @param name
     * @return
     */
    List<Cart> findCartListFromRedis(String name);

    /**
     * 从缓存中获取收藏集合
     * @param name
     * @return
     */
    List<Collect> findCollectListFromRedi(String name);
}

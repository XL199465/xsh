package cn.ithcast.core.controller;

import cn.ithcast.core.pojo.order.OrderItem;
import cn.ithcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Cart;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
@SuppressWarnings("all")
public class CartController {

    // 注入CartService对象
    @Reference
    private CartService cartService;

    /**
     * 加入购物车
     *
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9003", allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, int num, HttpServletRequest request, HttpServletResponse response) {
        try {

            // 获取cookie
            List<Cart> oldCartList = null;
            Cookie[] cookies = request.getCookies();
            if (null != cookies && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if ("cart".equals(cookie.getName())) {
                        String decode = URLDecoder.decode(cookie.getValue(), "UTF-8");
                        oldCartList = JSON.parseArray(decode, Cart.class);
                    }
                }
            }
            if (null == oldCartList) {
                oldCartList = new ArrayList<>();
            }

            // 封装新的购物车
            Cart newCart = new Cart();
            // 封装商家id到购物车中
            newCart.setSellerId(cartService.findItemByItemId(itemId).getSellerId());
            // 封装购物项到购物车中
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setNum(num);
            newOrderItem.setItemId(itemId);
            List<OrderItem> newOrderItemList = new ArrayList<>();
            newOrderItemList.add(newOrderItem);
            newCart.setOrderItemList(newOrderItemList);

            // 追加新的购物车到cokkie中
            int newCartIndex = oldCartList.indexOf(newCart);
            if (newCartIndex != -1) {
                // 说明cookie中含有新的购物车
                Cart oldCart = oldCartList.get(newCartIndex);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                int newOrderItemIndex = oldOrderItemList.indexOf(newOrderItem);
                if (newOrderItemIndex != -1) {
                    // 说明含有新购物项,改变数量
                    OrderItem oldOrderItem = oldOrderItemList.get(newOrderItemIndex);
                    oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());
                } else {
                    oldOrderItemList.add(newOrderItem);
                }
            } else {
                // 说明cookie中不含有新的购物车
                oldCartList.add(newCart);
            }


            // 判断用户是否登录  获取到用户名称
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!"anonymousUser".equals(name)) {
                // 说明登录了,再将整合后的购物车集合存入redis中
                cartService.addCartListToRedis(oldCartList, name);
                // 清空cokkie中的数据
                Cookie cookie = new Cookie("cart", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);

            } else {
                // 保存到cookie中
                String value = JSON.toJSONString(oldCartList);
                value = URLEncoder.encode(value, "UTF-8");
                Cookie cookie = new Cookie("cart", value);
                cookie.setMaxAge(60 * 60 * 24 * 3);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            return new Result(true, "访问成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "访问失败");
        }
    }

    /**
     * 初始化购物车列表
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request) throws UnsupportedEncodingException {
        // 获取cookie中的购物车
        Cookie[] cookies = request.getCookies();
        // 创建购物车
        List<Cart> cartList = null;
        // 判断cookies是否为空
        if (null != cookies && cookies.length > 0) {
            // 不为空  获取购物车
            for (Cookie cookie : cookies) {
                if ("cart".equals(cookie.getName())) {
                    String encode = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    cartList = JSON.parseArray(encode, Cart.class);
                }
            }
        }

        // 判断用户是否登录
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(name)) {
            // 说明已经登录 追加cookie中的购物车集合到缓存中的购物车集合
            if (null != cartList) {
                cartService.addCartListToRedis(cartList, name);
            }

            // 从缓存中获取购物车列表
            cartList = cartService.findCartListFromRedis(name);
        }


        // 运行到这里,cartList可能为null(1.cokkie不存在 2.cokkie中没有购物车),也可能不为null,所以要判断
        if (null != cartList) {
            // 封装数据
            cartList = cartService.findCartList(cartList);
        }

        // 返回结果
        return cartList;
    }
}

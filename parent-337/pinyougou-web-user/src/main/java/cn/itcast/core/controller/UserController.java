package cn.itcast.core.controller;

import cn.itcast.core.common.utils.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.user.User;
import cn.ithcast.core.service.CartService;
import cn.ithcast.core.service.GoodsService;
import cn.ithcast.core.service.ItemsearchService;
import cn.ithcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.sun.deploy.net.URLEncoder;
import entity.Cart;
import entity.Collect;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@SuppressWarnings("all")
public class UserController {

    // 注入UserService对象
    @Reference
    private UserService userService;
    //注入goodsService对象
    @Reference
    private ItemsearchService itemsearchService;

    //注入cartService
    @Reference
    private CartService cartService;

    /**
     * 获取验证码
     */
    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {

        // 首先判断输入的手机号格式是否正确
        if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            return new Result(false, "手机号码格式有误");
        }


        try {
            userService.sendCode(phone);
            return new Result(true, "发送验证码成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "发送验证码失败");
        }
    }

    /**
     * 注册
     */
    @RequestMapping("/add")
    public Result add(String smscode, @RequestBody User user) {
        try {
            userService.add(smscode, user);
            return new Result(true, "注册用户成功");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册用户失败");
        }
    }

   /*
        添加商品到收藏
    */
   @RequestMapping("/addToCollect")
   @CrossOrigin(origins = "http://localhost:9003", allowCredentials = "true")
    public Result addToCollect(Long itemId , HttpServletRequest request , HttpServletResponse response){
       try {
           //获取cookie
           List<Collect> oldCollectList = null;
           Cookie[] cookies = request.getCookies();
           if (null != cookies && cookies.length>0){
               for (Cookie cookie : cookies) {
                   if ("collect".equals(cookie.getName())){
                       String decode = URLDecoder.decode(cookie.getValue(), "UTF-8");
                       oldCollectList = JSON.parseArray(decode, Collect.class);
                   }
               }
           }
           if (null == oldCollectList) {
               oldCollectList = new ArrayList<>();
           }
           // 封装新的收藏
           Collect newCollect = new Collect();
           // 封装商家id到收藏中
           newCollect.setSellerId(cartService.findItemByItemId(itemId).getSellerId());
           // 封装购物项到收藏中
           OrderItem newOrderItem = new OrderItem();
           newOrderItem.setItemId(itemId);
           List<OrderItem> newOrderItemList = new ArrayList<>();
           newOrderItemList.add(newOrderItem);
           newCollect.setOrderItemList(newOrderItemList);

           // 追加新的收藏到cokkie中
           int newCollectIndex = oldCollectList.indexOf(newCollect);
           if (newCollectIndex != -1) {
               // 说明cookie中含有新的收藏
              Collect oldCollect = oldCollectList.get(newCollectIndex);
               List<OrderItem> oldOrderItemList = oldCollect.getOrderItemList();
               int newOrderItemIndex = oldOrderItemList.indexOf(newOrderItem);
               if (newOrderItemIndex != -1) {
                   // 说明含有新购物项,改变数量
                   OrderItem oldOrderItem = oldOrderItemList.get(newOrderItemIndex);
                   oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());
               } else {
                   oldOrderItemList.add(newOrderItem);
               }
           } else {
               // 说明cookie中不含有新的收藏
               oldCollectList.add(newCollect);
           }


           // 判断用户是否登录  获取到用户名称
           String name = SecurityContextHolder.getContext().getAuthentication().getName();
           if (!"anonymousUser".equals(name)) {
               // 说明登录了,再将整合后的收藏集合存入redis中
               cartService.addCollectListToRedi(oldCollectList, name);
               // 清空cokkie中的数据
               Cookie cookie = new Cookie("collect", null);
               cookie.setMaxAge(0);
               cookie.setPath("/");
               response.addCookie(cookie);

           } else {
               // 保存到cookie中
               String value = JSON.toJSONString(oldCollectList);
               value = java.net.URLEncoder.encode(value, "UTF-8");
               Cookie cookie = new Cookie("collect", value);
               cookie.setMaxAge(60 * 60 * 24 * 3);
               cookie.setPath("/");
               response.addCookie(cookie);
           }

           return new Result(true , "添加收藏成功!");
       } catch (Exception e) {
           e.printStackTrace();
           return new Result(false , "添加收藏失败!");
       }
   }

   /*
        查询显示所有
    */
    @RequestMapping("/findAll")
    public List<Collect> findAll(HttpServletRequest request){
        // 获取cookie中的购物车
        Cookie[] cookies = request.getCookies();
        // 创建收藏
        List<Collect> collectList = null;
        // 判断cookies是否为空
        if (null != cookies && cookies.length > 0) {
            // 不为空  获取收藏
            for (Cookie cookie : cookies) {
                if ("collect".equals(cookie.getName())) {
                    String encode = null;
                    try {
                        encode = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    collectList = JSON.parseArray(encode, Collect.class);
                }
            }
        }

        // 判断用户是否登录
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(name)) {
            // 说明已经登录 追加cookie中的收藏集合到缓存中的收藏集合
            if (null != collectList) {
                cartService.addCollectListToRedi(collectList, name);
            }

            // 从缓存中获取收藏列表
            collectList = cartService.findCollectListFromRedi(name);
        }


        // 运行到这里,cartList可能为null(1.cokkie不存在 2.cokkie中没有收藏),也可能不为null,所以要判断
        if (null != collectList) {
            // 封装数据
            collectList = cartService.findCollectLists(collectList);
        }

        // 返回结果
        return collectList;

    }



}

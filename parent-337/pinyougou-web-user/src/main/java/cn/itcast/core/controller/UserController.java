package cn.itcast.core.controller;

import cn.itcast.core.common.utils.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.user.User;
import cn.ithcast.core.service.ItemsearchService;
import cn.ithcast.core.service.OrderService;
import cn.ithcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import cn.itcast.core.pojogroup.Orderpp;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    //注入OrderService对象
    @Reference
    private OrderService orderService;

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
    public Result addToCollect(Integer itemId ){
       try {
           itemsearchService.addToCollect(itemId);
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
    public List<Item> findAll(){
        List<Item> itemList = new ArrayList<>();
        itemList.add(itemsearchService.findAll());

       return itemList;

    }
    //查询我的订单
    @RequestMapping("/findAllOrders")
    public List<Orderpp>findAllOrders(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.findAllOrders(name);
    }
}

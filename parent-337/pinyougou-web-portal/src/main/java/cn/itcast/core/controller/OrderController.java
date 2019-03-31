package cn.itcast.core.controller;

import cn.itcast.core.pojo.order.Order;
import cn.ithcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@SuppressWarnings("all")
public class OrderController {

    // 注入OrderService对象
    @Reference
    private OrderService orderService;

    /**
     * 保存到订单
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Order order) {
        try {
            // 获取当前登录人,并封装到Order订单对象中
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(name);
            // 保存
            orderService.add(order);
            return new Result(true, "保存订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存订单失败");
        }
    }
}

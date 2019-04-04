package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill")
public class SeckillController {
    @Reference
    private SeckillService seckillService;

    @RequestMapping("/search")
    public PageResult search(Integer pageNum,Integer pageSize,String status){
        return seckillService.search(pageNum,pageSize,status);


    }
    @RequestMapping("/orderSearch")
    public PageResult orderSearch(Integer pageNum, Integer pageSize, @RequestBody SeckillOrder seckillOrder){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return seckillService.orderSearch(pageNum,pageSize,seckillOrder,name);
    }

}

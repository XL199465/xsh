package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillgoods")
public class seckillGoodsController {
    @Reference
    private SeckillService seckillService;

    @RequestMapping("/searchY")
    public PageResult searchY(Integer pageNum,Integer pageSize){
        return seckillService.searchY(pageNum,pageSize);

    }
    @RequestMapping("/findOne")
    public SeckillGoods findOne(String ss){
        String[] split = ss.split("=", 2);
        Long id= Long.valueOf(split[1]);

        return seckillService.findOne(id);
    }
}

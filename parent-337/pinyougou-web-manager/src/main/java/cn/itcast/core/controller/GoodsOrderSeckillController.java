package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运营商秒杀
 */
@RestController
@RequestMapping("/goodsOrderSeckill")
public class GoodsOrderSeckillController {
    @Reference
    private SeckillService seckillService;

    //运营商查询秒杀订单
    @RequestMapping("/orderSearch")
    public PageResult orderSearch(Integer pageNum, Integer pageSize, @RequestBody SeckillOrder seckillOrder){
        return seckillService.operatororderSearch(pageNum,pageSize,seckillOrder);





    }
}

package cn.itcast.core.controller;

import cn.ithcast.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
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
}

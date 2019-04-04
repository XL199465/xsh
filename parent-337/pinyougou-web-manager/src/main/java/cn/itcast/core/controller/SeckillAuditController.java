package cn.itcast.core.controller;

import cn.itcast.core.service.SeckillAuditService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
public class SeckillAuditController {
    @Reference
    private SeckillAuditService seckillAuditService;

    /**
     * 秒杀提交的商品查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(Integer pageNum,Integer pageSize){

       return seckillAuditService.search(pageNum,pageSize);

    }

    @RequestMapping("/addAudit")
    public Result addAudit(Long[] ids){
      return   seckillAuditService.addAudit(ids);

    }

}

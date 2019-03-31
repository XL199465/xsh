package cn.itcast.core.controller;


import cn.itcast.core.pojo.good.Goods;
import cn.ithcast.core.service.GoodsPutawayService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goodsPutaway")
public class GoodsPutawayController {
    @Reference
    private GoodsPutawayService goodsPutawayService;

    @RequestMapping("/search")
    public PageResult search(Integer pageNum,Integer pageSize,@RequestBody Goods goods){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return  goodsPutawayService.search(pageNum,pageSize,goods,name);

    }

    @RequestMapping("/putaway")
    public Result putaway(Long[] selectIds){

        try {
            goodsPutawayService.putaway(selectIds);
            return new Result(true,"上架成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"程序错误");
        }

    }
}

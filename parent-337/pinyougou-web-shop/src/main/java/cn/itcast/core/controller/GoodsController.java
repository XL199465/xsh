package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojogroup.GoodsVo;
import cn.ithcast.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
@SuppressWarnings("all")
public class GoodsController {

    //注入GoodsService对象
    @Reference
    private GoodsService goodsService;

    /**
     * 添加商品
     *
     * @param goodsVo
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsVo goodsVo) {
        // 由于商品表需要指明商家名,所以要在controller层获取商家名 sellerId
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goodsVo.getGoods().setSellerId(sellerId);

        try {
            goodsService.add(goodsVo);
            return new Result(true, "添加商品成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加商品失败");
        }
    }

    /**
     * 分页+条件查询商品
     */
    @RequestMapping("/search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody Goods goods) {
        // 设置商家id
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String sellerId = securityContext.getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(pageNum, pageSize, goods);
    }

    /**
     * 根据商品id查询
     */
    @RequestMapping("/findOne")
    public GoodsVo findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 修改商品
     */
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsVo goodsVo) {
        // 获取当前商家名,并存入商品对象
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Goods goods = goodsVo.getGoods();
        goods.setSellerId(name);

        try {
            goodsService.update(goodsVo);
            return new Result(true, "修改商品成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改商品失败");
        }
    }

    /**
     * 添加秒杀
     * @param ids
     * @return
     */
    @RequestMapping("/addSeconds")
    public Result addSeconds(Long[] ids, @RequestBody SeckillGoods seckillGoods){

           return goodsService.addSeconds(ids,seckillGoods);
    }
}

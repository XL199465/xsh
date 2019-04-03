package cn.itcast.core.controller;


import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.order.Order;
import cn.ithcast.core.service.ItemCatService;
import cn.ithcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
@SuppressWarnings("all")
public class ItemCatController {

    // 注入ItemCatService对象
    @Reference
    private ItemCatService itemCatService;

    /**
     * 根据父id查询
     */
    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId) {
        return itemCatService.findByParentId(parentId);
    }

    /**
     * 添加商品分类
     */
    @RequestMapping("/add")
    public Result add(@RequestBody ItemCat itemCat) {
        try {
            itemCatService.add(itemCat);
            return new Result(true, "添加商品分类成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加商品分类失败");
        }
    }

    /**
     * 修改商品分类之数据回显
     */
    @RequestMapping("/findOne")
    public ItemCat findOne(Long id) {
        return itemCatService.findOne(id);
    }

    /**
     * 修改商品
     */
    @RequestMapping("/update")
    public Result update(@RequestBody ItemCat itemCat) {
        try {
            itemCatService.update(itemCat);
            return new Result(true, "修改商品分类成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改商品分类失败");
        }
    }

    /**
     * 删除商品分类
     */
    @RequestMapping("/del")
    public Result del(Long[] selectIds) {
        return itemCatService.del(selectIds);
    }

    /**
     * 查询所有
     */
    @RequestMapping("/findAll")
    public List<ItemCat> findAll() {
        return itemCatService.findAll();
    }


    @Reference
    private OrderService orderService;

   /* @RequestMapping("/findAllOrders")
    public List<Order> findAllOrders(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println(name);
        return orderService.findAllOrders(name);
    }*/
}

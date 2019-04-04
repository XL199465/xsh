package cn.itcast.core.controller;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    // 注入SellerService对象
    @Reference
    private SellerService sellerService;

    /**
     * 分页+查询
     */
    @RequestMapping("/search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody(required = false) Seller seller) {
        return sellerService.search(pageNum, pageSize, seller);
    }

    /**
     * 商品详情
     */
    @RequestMapping("/findOne")
    public Seller findOne(String sellerId) {
        return sellerService.findOne(sellerId);
    }

    /**
     * 更改商品状态
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId, String status) {
        try {
            sellerService.updateStatus(sellerId, status);
            return new Result(true, "更改商品状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更改商品状态失败");
        }
    }
}

package cn.ithcast.core.controller;

import cn.ithcast.core.pojo.seller.Seller;
import cn.ithcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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

    @RequestMapping("/test")
    public Map<String, List> guFindData() {
        Map<String, List> map = new HashMap<String, List>();
       /* //获取商家登录id
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //查询销售额信息
        Map<String, Double> saleDataMap = orderService.findSaleData(start, end, sellerId);*/

       /* Map<String, Double> saleDataMap = new HashMap<>();
        saleDataMap.put("2003-12-3", 112321.0D);
        saleDataMap.put("2004-12-3", 122321.0D);
        saleDataMap.put("2005-12-3", 132321.0D);


        //用来封装商家名称
        ArrayList<String> xAxisList = new ArrayList<>();
        //用来封装销量
        ArrayList<Double> seriesSaleList = new ArrayList<>();

        //遍历数据，封装到新的map中
        Set<String> keySet = saleDataMap.keySet();
        for (String key : keySet) {
            //添加日期
            xAxisList.add(key);
            //添加销售额
            seriesSaleList.add(saleDataMap.get(key));
        }*/

        // 查询商家名称集合
        List<Seller> sellerList = sellerService.findAll();

        // 查询销量


        map.put("xAxisList", sellerList);
//        map.put("seriesSaleList", seriesSaleList);
        return map;
    }
}

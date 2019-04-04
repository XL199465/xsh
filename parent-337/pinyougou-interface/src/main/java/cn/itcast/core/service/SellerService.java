package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import entity.PageResult;

public interface SellerService {

    /**
     * 商家入驻
     * @param seller
     */
    void add(Seller seller);

    /**
     * 分页+查询
     * @param pageNum
     * @param pageSize
     * @param seller
     * @return
     */
    PageResult search(Integer pageNum, Integer pageSize, Seller seller);

    /**
     * 查询商品详情
     * @param sellerId
     * @return
     */
    Seller findOne(String sellerId);

    /**
     * 修改商品状态
     * @param sellerId
     * @param status
     */
    void updateStatus(String sellerId, String status);
}

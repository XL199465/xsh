package cn.ithcast.core.service;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojogroup.GoodsVo;
import entity.PageResult;

public interface GoodsService {

    /**
     * 添加商品
     * @param goodsVo
     */
    void add(GoodsVo goodsVo);

    /**
     * 分页+条件查询商品
     * @param pageNum
     * @param pageSize
     * @param goods
     * @return
     */
    PageResult search(Integer pageNum, Integer pageSize, Goods goods);

    /**
     * 根据商品id查询
     * @param id
     * @return
     */
    GoodsVo findOne(Long id);

    /**
     * 修改商品
     * @param goodsVo
     */
    void update(GoodsVo goodsVo);

    /**
     * 运营商批量修改商品状态
     * @param selectIds
     * @param id
     */
    void updateStatus(Long[] selectIds, String id);

    /**
     * 运营商批量删除商品
     * @param selectIds
     */
    void del(Long[] selectIds);
}

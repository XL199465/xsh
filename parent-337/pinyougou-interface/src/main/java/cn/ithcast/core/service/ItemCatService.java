package cn.ithcast.core.service;

import cn.ithcast.core.pojo.item.ItemCat;
import entity.Result;

import java.util.List;

public interface ItemCatService {

    /**
     * 根据父id进行查询
     *
     * @param parentId
     * @return
     */
    List<ItemCat> findByParentId(Long parentId);

    /**
     * 添加商品分类
     *
     * @param itemCat
     */
    void add(ItemCat itemCat);

    /**
     * 修改商品分类之数据回显
     *
     * @param id
     * @return
     */
    ItemCat findOne(Long id);

    /**
     * 修改商品
     *
     * @param itemCat
     */
    void update(ItemCat itemCat);

    /**
     * 删除商品分类
     *
     * @param selectIds
     * @return
     */
    Result del(Long[] selectIds);

    /**
     * 查询所有
     * @return
     */
    List<ItemCat> findAll();
}

package cn.ithcast.core.service;

import cn.itcast.core.pojo.good.Brand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 查询所有品牌,并显示在浏览器
     *
     * @return
     */
    List<Brand> findAll();

    /**
     * 分页展示
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findPage(Integer pageNum, Integer pageSize);

    /**
     * 新增品牌
     *
     * @param brand
     */
    void add(Brand brand);

    /**
     * 修改品牌之数据回显
     *
     * @param id
     * @return
     */
    Brand findOne(Long id);

    /**
     * 修改品牌之修改品牌
     *
     * @param brand
     */
    void update(Brand brand);


    /**
     * 删除品牌
     *
     * @param ids
     */
    void del(Long[] ids);

    /**
     * 条件查询
     */
    PageResult search(Integer pageNum, Integer pageSize, Brand brand);

    /**
     * 查询id和name,存入一个map集合 并将多个这样的集合存入list集合中
     */
    List<Map> selectOptionList();
}

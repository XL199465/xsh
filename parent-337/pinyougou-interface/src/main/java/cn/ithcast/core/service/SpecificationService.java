package cn.ithcast.core.service;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojogroup.SpecificationVo;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface SpecificationService {

    /**
     * 分页+条件查询规格
     *
     * @param pageNum
     * @param pageSize
     * @param specification
     * @return
     */
    PageResult search(Integer pageNum, Integer pageSize, Specification specification);

    /**
     * 添加规格及规格选项
     */
    public void add(SpecificationVo specificationVo);

    /**
     * 修改规格之数据回显
     *
     * @param id
     * @return
     */
    SpecificationVo findOne(Long id);

    /**
     * 修改规格之修改规格
     *
     * @param specificationVo
     */
    void update(SpecificationVo specificationVo);

    /**
     * 删除规格
     *
     * @param ids
     */
    void del(Long[] ids);

    /**
     * 查询规格id和规格的名字,并存入map集合中
     */
    List<Map> selectOptionList();
}

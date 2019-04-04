package cn.itcast.core.service;

import cn.itcast.core.pojo.template.TypeTemplate;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    /**
     * 分页+条件查询
     *
     * @param pageNum
     * @param pageSize
     * @param typeTemplate
     * @return
     */
    PageResult search(Integer pageNum, Integer pageSize, TypeTemplate typeTemplate);


    /**
     * 新增模板
     *
     * @param typeTemplate
     */
    void add(TypeTemplate typeTemplate);

    /**
     * 修改模板之数据回显
     *
     * @param id
     * @return
     */
    TypeTemplate findOne(Long id);


    /**
     * 修改模板
     *
     * @param typeTemplate
     */
    void update(TypeTemplate typeTemplate);

    /**
     * 删除模板
     *
     * @param selectIds
     * @return
     */
    void del(Long[] selectIds);

    /**
     * 模板下拉框
     */
    List<Map> findTypeTemplateList();

    /**
     * 自定义根据id查询  返回值为List<Map>
     */
    public List<Map> findSpecListById(Long id);


}

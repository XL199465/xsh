package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import cn.ithcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    // 注入TypeTemplateDao对象
    @Autowired
    private TypeTemplateDao typeTemplateDao;

    // 注入SpecificationOptionDao对象
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    // 注入RedisTemplate对象
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页+条件查询
     * 同时将结果添加到缓存中
     *
     * @param pageNum
     * @param pageSize
     * @param typeTemplate
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, TypeTemplate typeTemplate) {

        // 添加品牌列表和规格列表到缓存中
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);

        for (TypeTemplate template : typeTemplates) {
            String brandIds = template.getBrandIds();
            List<Map> list = JSON.parseArray(brandIds, Map.class);
            redisTemplate.boundHashOps("brandList").put(template.getId(), list);

            List<Map> specList = findSpecListById(template.getId());
            redisTemplate.boundHashOps("specList").put(template.getId(), specList);
        }


        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 创建条件查询对象
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();

        // 判断
        if (null != typeTemplate) {
            if (null != typeTemplate.getName() && typeTemplate.getName().trim().length() > 0) {
                TypeTemplateQuery.Criteria criteria = typeTemplateQuery.createCriteria();
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
        }

        // 获取分页对象
        Page<TypeTemplate> typeTemplateList = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);

        // 返回结果
        return new PageResult(typeTemplateList.getTotal(), typeTemplateList.getResult());
    }

    /**
     * 新增模板
     *
     * @param typeTemplate
     */
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 修改模板之数据回显
     *
     * @param id
     * @return
     */
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 修改模板
     *
     * @param typeTemplate
     */
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    /**
     * 删除模板
     *
     * @param selectIds
     * @return
     */
    @Override
    public void del(Long[] selectIds) {

        if (null != selectIds) {
            for (Long selectId : selectIds) {
                typeTemplateDao.deleteByPrimaryKey(selectId);
            }
        }
    }

    /**
     * 模板下拉框
     */
    @Override
    public List<Map> findTypeTemplateList() {
        return typeTemplateDao.findTypeTemplateList();
    }

    /**
     * 自定义根据id查询 返回值为List<Map>
     *
     * @param id
     * @return
     */
    @Override
    public List<Map> findSpecListById(Long id) {
        // 先根据模板id查询到模板对象
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        // 获取模板对象的specIds字段值
        String specIds = typeTemplate.getSpecIds();
        // 将json字符串转换成为集合  [{"id":33,"text":"电视屏幕尺寸"}]
        List<Map> specList = JSON.parseArray(specIds, Map.class);

        // 遍历specList
        for (Map map : specList) {
            // 创建条件查询对象
            SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
            // 添加条件查询
            criteria.andSpecIdEqualTo((long) (Integer) map.get("id"));
            // 执行查询
            List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(specificationOptionQuery);
            // 将结果添加到map集合中 [{"id":33,"text":"电视屏幕尺寸", "options":[{}, {}, {}]}]
            map.put("options", specificationOptionList);
        }

        return specList;
    }
}

package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.ithcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

//使用dubbo的注解@Service将组件注入容器中
@Service
public class BrandServiceImpl implements BrandService {

    // 注入BrandDao对象
    @Autowired
    private BrandDao brandDao;

    /**
     * 查询所有品牌,并显示在浏览器
     *
     * @return
     */
    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    /**
     * 分页展示
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        Page<Brand> brandPage = (Page<Brand>) brandDao.selectByExample(null);

        return new PageResult(brandPage.getTotal(), brandPage.getResult());
    }

    /**
     * 新增品牌
     *
     * @param brand
     */
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    /**
     * 修改品牌之数据回显
     *
     * @param id
     * @return
     */
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    /**
     * 修改品牌之修改品牌
     *
     * @param brand
     */
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    /**
     * 删除品牌
     *
     * @param ids
     */
    @Override
    public void del(Long[] ids) {
        for (Long id : ids) {
            brandDao.deleteByPrimaryKey(id);
        }
    }

    /**
     * 条件查询
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 创建条件查询的实体类的对象
        BrandQuery brandQuery = new BrandQuery();
        // 判断是否含有条件
        if (brand != null) {

            // 说明含有条件,那么创建内部类Criteria对象,封装条件
            BrandQuery.Criteria criteria = brandQuery.createCriteria();

            // 先判断姓名
            if (brand.getName() != null && brand.getName().trim().length() > 0) {
                criteria.andNameLike("%" + brand.getName().trim() + "%");
            }
            // 然后判断首字母
            if (brand.getFirstChar() != null && brand.getFirstChar().trim().length() > 0) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }

        // 获取分页对象
        Page<Brand> brandPage = (Page<Brand>) brandDao.selectByExample(brandQuery);

        return new PageResult(brandPage.getTotal(), brandPage.getResult());
    }

    /**
     * 查询id和name,存入一个map集合 并将多个这样的集合存入list集合中
     * @return
     */
    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }
}

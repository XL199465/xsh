package cn.itcast.core.dao.good;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface BrandDao {
    int countByExample(BrandQuery example);

    int deleteByExample(BrandQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(Brand record);

    int insertSelective(Brand record);

    List<Brand> selectByExample(BrandQuery example);

    Brand selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Brand record, @Param("example") BrandQuery example);

    int updateByExample(@Param("record") Brand record, @Param("example") BrandQuery example);

    int updateByPrimaryKeySelective(Brand record);

    int updateByPrimaryKey(Brand record);

    /**
     * 查询id和name,存入一个map集合 并将多个这样的集合存入list集合中
     * @return
     */
    List<Map> selectOptionList();
}
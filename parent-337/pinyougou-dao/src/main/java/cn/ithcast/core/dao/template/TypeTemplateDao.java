package cn.ithcast.core.dao.template;

import cn.ithcast.core.pojo.template.TypeTemplate;
import cn.ithcast.core.pojo.template.TypeTemplateQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TypeTemplateDao {
    int countByExample(TypeTemplateQuery example);

    int deleteByExample(TypeTemplateQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(TypeTemplate record);

    int insertSelective(TypeTemplate record);

    List<TypeTemplate> selectByExample(TypeTemplateQuery example);

    TypeTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TypeTemplate record, @Param("example") TypeTemplateQuery example);

    int updateByExample(@Param("record") TypeTemplate record, @Param("example") TypeTemplateQuery example);

    int updateByPrimaryKeySelective(TypeTemplate record);

    int updateByPrimaryKey(TypeTemplate record);

    /**
     * 模板下拉框
     * @return
     */
    List<Map> findTypeTemplateList();
}
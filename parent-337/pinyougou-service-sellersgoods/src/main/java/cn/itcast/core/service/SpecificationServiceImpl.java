package cn.itcast.core.service;

import cn.itcast.core.common.utils.ExcelUtil;
import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.pojogroup.SpecificationVo;
import cn.ithcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

// 创建SpecificationService组件并存入spring容器
@Service
public class SpecificationServiceImpl implements SpecificationService {

    // 注入SpecificationDao对象
    @Autowired
    private SpecificationDao specificationDao;

    // 注入SpecificationOptionDao对象
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    /**
     * 分页+条件查询规格
     *
     * @param pageNum
     * @param pageSize
     * @param specification
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Specification specification) {
        // 分页助手开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 创建规格条件查询对象
        SpecificationQuery specificationQuery = new SpecificationQuery();

        // 判断
        if (null != specification) {
            SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();
            // 说明含有条件,那么添加条件
            if (specification.getSpecName() != null && specification.getSpecName().trim().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
        }

        // 执行方法,获得规格集合,强转成分页对象
        Page<Specification> specificationList = (Page<Specification>) specificationDao.selectByExample(specificationQuery);

        // 封装分页结果对象并返回
        return new PageResult(specificationList.getTotal(), specificationList.getResult());
    }

    /**
     * 添加规格及规格选项
     *
     * @param specificationVo
     */
    @Override
    public void add(SpecificationVo specificationVo) {
        // 获取规格对象,并保存规格 由于添加了主键回显,所以可以获取到规格的id
        Specification specification = specificationVo.getSpecification();
        specificationDao.insertSelective(specification);

        // 获取规格的id
        Long specificationId = specificationVo.getSpecification().getId();

        // 根据规格id添加规格选项
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            // 设置规格选项的id为规格对象的id
            specificationOption.setSpecId(specificationId);
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    /**
     * 修改规格之数据回显
     *
     * @param id
     * @return
     */
    @Override
    public SpecificationVo findOne(Long id) {
        SpecificationVo specificationVo = new SpecificationVo();

        // 封装规格
        Specification specification = specificationDao.selectByPrimaryKey(id);
        specificationVo.setSpecification(specification);

        // 封装规格对应的规格选项  属于条件查询 所以先创建条件查询对象, 创建内部类Criteria对象  然后添加对应的条件  最后执行方法
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(specificationOptionQuery);
        specificationVo.setSpecificationOptionList(specificationOptionList);

        return specificationVo;
    }

    /**
     * 修改规格之修改规格
     *
     * @param specificationVo
     */
    @Override
    public void update(SpecificationVo specificationVo) {
        // 首先修改规格
        Specification specification = specificationVo.getSpecification();
        specificationDao.updateByPrimaryKeySelective(specification);

        // 然后删除所有规格选项
        Long specificationId = specification.getId();
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
        criteria.andSpecIdEqualTo(specificationId);
        specificationOptionDao.deleteByExample(specificationOptionQuery);

        // 最后再保存规格选项
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            specificationOption.setSpecId(specificationId);
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    /**
     * 删除规格
     *
     * @param ids
     */
    @Override
    public void del(Long[] ids) {
        for (Long id : ids) {
            // 删除规格
            specificationDao.deleteByPrimaryKey(id);

            // 删除规格对应的规格选项
            SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(specificationOptionQuery);
        }
    }

    /**
     * 查询规格id和规格的名字,并存入map集合中
     *
     * @return
     */
    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }

    /**
     * Excel表格数据导入数据库
     *
     * @param bytes
     * @return
     */
    @Override
    public String ajaxUploadExcel(byte[] bytes) {
        System.out.println("得到数据文件");

        // 判断文件是否为空
        if (null == bytes) {
            try {
                throw new Exception("文件不存在！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 获取文件输入流
        System.out.println("加载流");
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 封装出list集合数据
        System.out.println("封装list集合数据");
        List<List<Object>> list = null;
        try {
            list = new ExcelUtil().getBankListByExcel(inputStream, "jjj");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 调用service相应方法进行数据保存到数据库中
        Specification specification = new Specification();
        for (int i = 0; i < list.size(); i++) {
            List<Object> lo = list.get(i);
            System.out.println("遍历" + list.get(i));

            // 定义一个临时的Specification对象
            Specification temp = null;
            try {
                temp = specificationDao.selectByPrimaryKey(Long.valueOf(String.valueOf(lo.get(0))));
            } catch (Exception e) {
                System.out.println("没有新增");
            }

            // 封装Specification对象
            specification.setId(Long.valueOf(String.valueOf(lo.get(0))));
            specification.setSpecName(String.valueOf(lo.get(1)));
            specification.setAuditstatus(String.valueOf(lo.get(2)));

            // 判断temp对象是否为空,为空,则新增,不为空,则修改
            if (null == temp) {
                // 新增
                System.out.println("新增了数据");
                specificationDao.insertSelective(specification);
            } else {
                // 修改
                System.out.println("修改了数据");
                specificationDao.updateByPrimaryKeySelective(specification);
            }

        }
        return "success";
    }
}

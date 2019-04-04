package cn.ithcast.core.controller;

import cn.ithcast.core.pojo.specification.Specification;
import cn.ithcast.core.pojogroup.SpecificationVo;
import cn.ithcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    // 注入SpecificationService对象
    @Reference
    private SpecificationService specificationService;

    /**
     * 分页+条件查询规格
     */
    @RequestMapping("/search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody(required = false) Specification specification) {
        return specificationService.search(pageNum, pageSize, specification);
    }

    /**
     * 添加规格及规格选项
     */
    @RequestMapping("/add")
    public Result add(@RequestBody SpecificationVo specificationVo) {
        System.out.println(123);
        try {
            specificationService.add(specificationVo);
            return new Result(true, "添加规格成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加规格失败");
        }
    }

    /**
     * 修改规格之数据回显
     */
    @RequestMapping("/findOne")
    public SpecificationVo findOne(Long id) {
        return specificationService.findOne(id);
    }

    /**
     * 修改规格之修改规格
     */
    @RequestMapping("/update")
    public Result update(@RequestBody SpecificationVo specificationVo) {
        try {
            specificationService.update(specificationVo);
            return new Result(true, "修改规格成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改规格失败");
        }
    }

    /**
     * 删除规格
     */
    @RequestMapping("/del")
    public Result del(Long[] ids) {
        try {
            specificationService.del(ids);
            return new Result(true, "删除规格成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除规格失败");
        }
    }

    /**
     * 查询规格id和规格的名字,并存入map集合中
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return specificationService.selectOptionList();
    }

    /**
     * Excel表格数据导入数据库
     */
    @RequestMapping("/ajaxUpload")
    public String ajaxUploadExcel(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        return specificationService.ajaxUploadExcel(bytes);
    }
}

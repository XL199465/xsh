package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

// 由于要响应json格式的数据,又要注册组件到容器,所以使用注解@RestController
@RestController
@RequestMapping("/brand")
public class BrandController {

    // 注入BrandService 这里要使用dubbo的注解@Reference
    @Reference
    private BrandService brandService;

    /**
     * 查询所有品牌,并显示在浏览器
     */
    @RequestMapping("/findAll")
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    /**
     * 分页展示
     */
    @RequestMapping("/findPage")
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        return brandService.findPage(pageNum, pageSize);
    }

    /**
     * 新增品牌
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Brand brand) {
        try {
            brandService.add(brand);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }
    }

    /**
     * 修改品牌之数据回显
     */
    @RequestMapping("/findOne")
    public Brand findOne(Long id) {
        Brand brand = brandService.findOne(id);
        return brand;
    }

    /**
     * 修改品牌之修改品牌
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand) {
        try {
            brandService.update(brand);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 删除品牌
     */
    @RequestMapping("/del")
    public Result del(Long[] ids) {
        try {
            brandService.del(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 条件查询
     */
    @RequestMapping("/search")
    // brand不是必须的参数,当没有条件的时候,就可以没有
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody(required = false) Brand brand) {
        return brandService.search(pageNum, pageSize, brand);
    }

    /**
     * 查询id和name,存入一个map集合 并将多个这样的集合存入list集合中
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }

    /**
     * 品牌Excel表导入数据库
     */
    @RequestMapping("/ajaxUpload")
    public String ajaxUploadExcel(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        return brandService.ajaxUploadExcel(bytes);
    }
}

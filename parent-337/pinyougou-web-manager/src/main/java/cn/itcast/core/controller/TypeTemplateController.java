package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
@SuppressWarnings("all")
public class TypeTemplateController {

    // 注入TypeTemplateService对象
    @Reference
    private TypeTemplateService typeTemplateService;

    /**
     * 分页+条件查询
     */
    @RequestMapping("/search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody(required = false) TypeTemplate typeTemplate) {
        return typeTemplateService.search(pageNum, pageSize, typeTemplate);
    }


    /*/**
     * 修改模板之数据回显
     *//*
//    @RequestMapping("/findOne")
//    public TypeTemplate findOne(Long id) {
//        typeTemplateService.findOne(id);
//    }*/

    /**
     * 新增模板
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate typeTemplate) {
        try {
            System.out.println(typeTemplate.getBrandIds());
            System.out.println(typeTemplate.getSpecIds());
            typeTemplateService.add(typeTemplate);
            return new Result(true, "新增模板成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "新增模板失败");
        }
    }

    /**
     * 修改模板之数据回显
     */
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id) {
        return typeTemplateService.findOne(id);
    }

    /**
     * 修改模板
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate) {
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true, "修改模板成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改模板失败");
        }
    }

    /**
     * 删除模板
     */
    @RequestMapping("/del")
    public Result del(Long[] selectIds) {
        try {
            typeTemplateService.del(selectIds);
            return new Result(true, "删除模板成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除模板失败");
        }
    }

    /**
     * 模板下拉框
     */
    @RequestMapping("/findTypeTemplateList")
    public List<Map> findTypeTemplateList() {
        return typeTemplateService.findTypeTemplateList();
    }
}

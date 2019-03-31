package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.Content;
import cn.ithcast.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
@SuppressWarnings("all")
public class ContentController {

    // 注入ContentService对象
    @Reference
    private ContentService contentService;

    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId) {
        return contentService.findByCategoryId(categoryId);
    }
}

package cn.ithcast.core.controller;

import cn.ithcast.core.service.ItemsearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
@SuppressWarnings("all")
public class ItemsearchController {

    @Reference
    private ItemsearchService itemsearchService;

    /**
     * 搜索
     */
    @RequestMapping("/search")
    public Map<String, Object> search(@RequestBody Map<String, String> searchMap) {
        return itemsearchService.search(searchMap);
    }
}

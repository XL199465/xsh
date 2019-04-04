package cn.ithcast.core.service;

import cn.ithcast.core.pojo.item.Item;

import java.util.Map;

public interface ItemsearchService {
    /**
     * 搜索
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String, String> searchMap);

    void addToCollect(Integer itemId);

    Item findAll();
}

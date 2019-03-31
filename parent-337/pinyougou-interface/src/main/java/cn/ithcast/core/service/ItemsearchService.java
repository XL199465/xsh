package cn.ithcast.core.service;

import java.util.Map;

public interface ItemsearchService {
    /**
     * 搜索
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String, String> searchMap);
}

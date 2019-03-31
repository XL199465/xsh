package cn.ithcast.core.service;

import cn.itcast.core.pojo.good.Goods;
import entity.PageResult;

public interface GoodsPutawayService {
    PageResult search(Integer pageNum, Integer pageSize, Goods goods,String name);

    void putaway(Long[] selectIds);
}

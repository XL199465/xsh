package cn.ithcast.core.service;

import cn.ithcast.core.pojo.good.Goods;
import entity.PageResult;
import entity.Result;

public interface GoodsPutawayService {
    PageResult search(Integer pageNum, Integer pageSize, Goods goods,String name);

    void putaway(Long[] selectIds);

    Result sole_out(Long[] selectIds);
}

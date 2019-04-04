package cn.ithcast.core.service;

import cn.ithcast.core.pojo.seckill.SeckillGoods;
import cn.ithcast.core.pojo.seckill.SeckillOrder;
import entity.PageResult;

public interface SeckillService {
    PageResult search(Integer pageNum, Integer pageSize, String status);

    PageResult orderSearch(Integer pageNum, Integer pageSize, SeckillOrder seckillOrder,String name);


    PageResult operatororderSearch(Integer pageNum, Integer pageSize, SeckillOrder seckillOrder);

    PageResult searchY(Integer pageNum, Integer pageSize);

    SeckillGoods findOne(Long id);
}

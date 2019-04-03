package cn.ithcast.core.service;

import entity.PageResult;

public interface SeckillService {
    PageResult search(Integer pageNum, Integer pageSize, String status);
}

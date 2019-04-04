package cn.itcast.core.service;

import entity.PageResult;
import entity.Result;

public interface SeckillAuditService {
    PageResult search(Integer pageNum, Integer pageSize);

    Result addAudit(Long[] ids);

}

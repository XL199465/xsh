package cn.ithcast.core.service;

import cn.itcast.core.pojo.user.User;
import entity.PageResult;

public interface UserService {
    /**
     * 发送验证码
     * @param phone
     */
    void sendCode(String phone);

    /**
     * 注册
     * @param smscode
     * @param user
     */
    void add(String smscode, User user);

    /**
     * 分页+条件查询
     * @param pageNum
     * @param pageSize
     * @param user
     * @return
     */
    PageResult search(Integer pageNum, Integer pageSize, User user);

    /**
     * 冻结用户
     * @param selectIds
     */
    void updateStatus(Long[] selectIds);
}

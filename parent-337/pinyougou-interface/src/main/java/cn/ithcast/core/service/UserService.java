package cn.ithcast.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojogroup.Orderpp;
import cn.itcast.core.pojogroup.UserVo;
import entity.PageResult;

import java.util.List;

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

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 用户活跃度统计
     * @return
     */
    UserVo countActivity();


    //查询我的订单
    List<Orderpp> findAllOrders(String name);
}

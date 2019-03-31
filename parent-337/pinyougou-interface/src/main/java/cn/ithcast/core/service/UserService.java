package cn.ithcast.core.service;

import cn.itcast.core.pojo.user.User;

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
}

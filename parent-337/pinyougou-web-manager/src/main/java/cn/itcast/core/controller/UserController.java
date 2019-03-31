package cn.itcast.core.controller;

import cn.itcast.core.pojo.user.User;
import cn.ithcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
@SuppressWarnings("all")
public class UserController {

    // 注入UserService对象
    @Reference
    private UserService userService;

    /**
     * 分页+条件查询
     */
    @RequestMapping("/search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody User user) {
        return userService.search(pageNum, pageSize, user);
    }

    /**
     * 冻结
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] selectIds) {
        try {
            userService.updateStatus(selectIds);
            return new Result(true, "冻结用户成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "冻结用户失败");
        }
    }
}

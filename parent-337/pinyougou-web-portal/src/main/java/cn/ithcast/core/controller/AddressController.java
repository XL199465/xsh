package cn.ithcast.core.controller;

import cn.ithcast.core.pojo.address.Address;
import cn.ithcast.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
@SuppressWarnings("all")
public class AddressController {

    // 注入AddressService对象
    @Reference
    private AddressService addressService;

    /**
     * 查找当前用户的地址列表
     */
    @RequestMapping("/findListByLoginUser")
    public List<Address> findListByLoginUser() {
        // 获取当前登录人
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        // 作为参数传递给AddressService层
        return addressService.findListByLoginUser(name);
    }
}

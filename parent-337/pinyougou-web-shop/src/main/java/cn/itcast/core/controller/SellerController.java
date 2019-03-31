package cn.itcast.core.controller;

import cn.itcast.core.pojo.seller.Seller;
import cn.ithcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/seller")
public class SellerController {

    // 注入SellerService对象
    @Reference
    private SellerService sellerService;

    /**
     * 商家入驻
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Seller seller) {

        // 密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(seller.getPassword());
        seller.setPassword(encodePassword);


        try {
            sellerService.add(seller);
            return new Result(true, "商家入驻成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "商家入驻失败");
        }
    }

    /**
     * 显示用户名和时间
     */
    @RequestMapping("/showName")
    public Map<String, Object> showName(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();

        SecurityContext securityContext = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        String username1 = securityContext.getAuthentication().getName();

        User user1 = (User) securityContext.getAuthentication().getPrincipal();
        String username2 = user1.getUsername();

        SecurityContext context = SecurityContextHolder.getContext();
        String username3 = context.getAuthentication().getName();

        User user2 = (User) context.getAuthentication().getPrincipal();
        String username4 = user2.getUsername();

        System.out.println(username1);
        System.out.println(username2);
        System.out.println(username3);
        System.out.println(username4);

        map.put("username", username1);
        map.put("curTime", new Date());

        return map;
    }
}

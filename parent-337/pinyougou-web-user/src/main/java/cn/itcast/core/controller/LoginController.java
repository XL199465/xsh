package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
@SuppressWarnings("all")
public class LoginController {

    /**
     * 显示用户名
     */
    @RequestMapping("name")
    public Map<String, Object> showName(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();

        SecurityContext securityContext = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        String username1 = securityContext.getAuthentication().getName();
        User principal1 = (User) securityContext.getAuthentication().getPrincipal();
        String username2 = principal1.getUsername();

        SecurityContext context = SecurityContextHolder.getContext();
        String username3 = context.getAuthentication().getName();
        User principal2 = (User) context.getAuthentication().getPrincipal();
        String username4 = principal2.getUsername();

        System.out.println(username1);
        System.out.println(username2);
        System.out.println(username3);
        System.out.println(username4);

        map.put("loginName", username1);

        return map;
    }
}

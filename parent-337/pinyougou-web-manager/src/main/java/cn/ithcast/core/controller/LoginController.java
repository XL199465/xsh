package cn.ithcast.core.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/showName")
    public Map<String, Object> showName(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();

        // 方式一
        HttpSession session = request.getSession();
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        String username1 = securityContext.getAuthentication().getName();

        User user1 = (User) securityContext.getAuthentication().getPrincipal();
        String username2 = user1.getUsername();

        // 方式二
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

package cn.itcast.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDetailServiceImpl implements UserDetailsService {


    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        System.out.println("我进来了");
        if (null != userService.findByUsername(username)) {
            System.out.println("1111");
            if ("1".equals(userService.findByUsername(username).getStatus())) {
                List<GrantedAuthority> list = new ArrayList<>();
                list.add(new SimpleGrantedAuthority("ROLE_USER"));
                // 真正需要授权的时候,就说明用户已经可以登录了,所以密码为空
                System.out.println("好的");
                return new User(username, "", list);
            } else {
                List<GrantedAuthority> list = new ArrayList<>();
                list.add(new SimpleGrantedAuthority(UUID.randomUUID()+""));
                System.out.println("didi");
                return new User(username, "", list);
            }
        }
        return null;
    }
}

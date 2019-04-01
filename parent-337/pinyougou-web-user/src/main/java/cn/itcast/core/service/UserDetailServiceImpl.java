package cn.itcast.core.service;

import cn.ithcast.core.service.UserService;
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

        if (null != userService.findByUsername(username)) {
            if ("1".equals(userService.findByUsername(username).getStatus())) {
                List<GrantedAuthority> list = new ArrayList<>();
                list.add(new SimpleGrantedAuthority("ROLE_USER"));
                // 真正需要授权的时候,就说明用户已经可以登录了,所以密码为空

                return new User(username, "", list);
            } else {
                List<GrantedAuthority> list = new ArrayList<>();
                list.add(new SimpleGrantedAuthority(UUID.randomUUID()+""));
                return new User(username, "", list);
            }
        }
        return null;
    }
}

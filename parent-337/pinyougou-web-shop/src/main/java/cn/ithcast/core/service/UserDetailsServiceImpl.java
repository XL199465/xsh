package cn.ithcast.core.service;

import cn.ithcast.core.pojo.seller.Seller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {


    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 从数据库中查询
        Seller seller = sellerService.findOne(username);
        if (null != seller) {
            if ("1".equals(seller.getStatus())) {
                List<GrantedAuthority> list = new ArrayList<>();
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_SELLER");
                list.add(simpleGrantedAuthority);

                return new User(seller.getSellerId(), seller.getPassword(), list);
            }
        }

        return null;
    }

    // 不从数据库中查询
       /* List<GrantedAuthority> list = new ArrayList<>();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_SELLER");

        list.add(simpleGrantedAuthority);

        User user = new User(username, "123456", list);
        return user;*/

}

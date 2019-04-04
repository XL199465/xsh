package cn.ithcast.core.service;

import cn.ithcast.core.pojo.address.Address;

import java.util.List;

public interface AddressService {

    /**
     * 查找当前用户的地址列表
     * @param name
     * @return
     */
    List<Address> findListByLoginUser(String name);
}

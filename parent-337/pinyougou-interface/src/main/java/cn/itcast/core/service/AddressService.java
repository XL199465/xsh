package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;

import java.util.List;

public interface AddressService {

    /**
     * 查找当前用户的地址列表
     * @param name
     * @return
     */
    List<Address> findListByLoginUser(String name);
}

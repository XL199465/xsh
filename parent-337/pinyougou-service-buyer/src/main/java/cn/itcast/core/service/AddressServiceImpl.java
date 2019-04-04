package cn.itcast.core.service;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    // 注入AddressDao对象
    @Autowired
    private AddressDao addressDao;

    @Override
    public List<Address> findListByLoginUser(String name) {
        // 创建条件查询对象
        AddressQuery addressQuery = new AddressQuery();
        AddressQuery.Criteria criteria = addressQuery.createCriteria();

        // 添加条件
        criteria.andUserIdEqualTo(name);

        // 执行查询
        List<Address> addressList = addressDao.selectByExample(addressQuery);

        // 返回结果
        return addressList;
    }
}

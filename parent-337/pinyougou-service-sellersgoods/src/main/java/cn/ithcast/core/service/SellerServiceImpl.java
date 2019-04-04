package cn.ithcast.core.service;

import cn.ithcast.core.dao.seller.SellerDao;
import cn.ithcast.core.pojo.seller.Seller;
import cn.ithcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service
public class SellerServiceImpl implements SellerService {

    // 注入SellerDao对象
    @Autowired
    private SellerDao sellerDao;

    /**
     * 商家入驻
     *
     * @param seller
     */
    @Override
    public void add(Seller seller) {
        // 设置商家状态为0
        seller.setStatus("0");

        // 设置入驻时间
        seller.setCreateTime(new Date());

        sellerDao.insertSelective(seller);
    }

    /**
     * 分页+查询
     *
     * @param pageNum
     * @param pageSize
     * @param seller
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Seller seller) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 创建条件查询对象
        SellerQuery sellerQuery = new SellerQuery();
        SellerQuery.Criteria criteria = sellerQuery.createCriteria();
        criteria.andStatusEqualTo(seller.getStatus());

        if (null != seller) {
            if (null != seller.getName() && seller.getName().trim().length() > 0) {
                criteria.andNameLike("%" + seller.getName() + "%");
            }

            if (null != seller.getNickName() && seller.getNickName().trim().length() > 0) {
                criteria.andNickNameLike("%" + seller.getNickName() + "%");
            }
        }

        // 获取分页对象
        Page<Seller> sellerList = (Page<Seller>) sellerDao.selectByExample(sellerQuery);

        return new PageResult(sellerList.getTotal(), sellerList.getResult());
    }

    /**
     * 查询商品详情
     *
     * @param sellerId
     * @return
     */
    @Override
    public Seller findOne(String sellerId) {
        return sellerDao.selectByPrimaryKey(sellerId);
    }

    /**
     * 修改商品状态
     *
     * @param sellerId
     * @param status
     */
    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }

    /**
     * 查询所有商家
     *
     * @return
     */
    @Override
    public List<Seller> findAll() {
        return sellerDao.selectByExample(null);
    }
}

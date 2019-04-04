package cn.itcast.core.service;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.pojo.seckill.SeckillOrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, String status) {
        PageHelper.startPage(pageNum, pageSize);
        //创建查询条件
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        Page<SeckillGoods> seckillGoods = null;
        //为零时查询全部
        if (status.equals("2")) {
            // 开启分页
            seckillGoodsQuery.createCriteria().andStatusEqualTo("0");
            seckillGoods = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
            return new PageResult(seckillGoods.getTotal(), seckillGoods.getResult());
        } else if (status.equals("1")) {
            seckillGoodsQuery.createCriteria().andStatusEqualTo("1");
            seckillGoods = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
            return new PageResult(seckillGoods.getTotal(), seckillGoods.getResult());
        }
        //查询普通商品

        seckillGoods = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(null);
        return new PageResult(seckillGoods.getTotal(), seckillGoods.getResult());
    }

    @Autowired
    private SeckillOrderDao seckillOrderDao;
    /**
     * 秒杀订单查询
     * @param pageNum
     * @param pageSize
     * @param seckillOrder
     * @return
     */
    @Override
    public PageResult orderSearch(Integer pageNum, Integer pageSize, SeckillOrder seckillOrder,String name) {



        PageHelper.startPage(pageNum, pageSize);
        String status = seckillOrder.getStatus();
        SeckillOrderQuery seckillOrderQuery = new SeckillOrderQuery();
        SeckillOrderQuery.Criteria criteria = seckillOrderQuery.createCriteria();
        criteria.andSellerIdEqualTo(name);
        if ("2".equals(status)) {
            Page<SeckillOrder> seckillOrders = (Page<SeckillOrder>) seckillOrderDao.selectByExample(seckillOrderQuery);
            return new PageResult(seckillOrders.getTotal(),seckillOrders.getResult());
        }
        criteria.andStatusEqualTo(status);
        Page<SeckillOrder> seckillOrders = (Page<SeckillOrder>)seckillOrderDao.selectByExample(seckillOrderQuery);
        return new PageResult(seckillOrders.getTotal(),seckillOrders.getResult());

    }

    //运营商秒杀商品
    @Override
    public PageResult operatororderSearch(Integer pageNum, Integer pageSize, SeckillOrder seckillOrder) {
        PageHelper.startPage(pageNum,pageSize);
        SeckillOrderQuery seckillOrderQuery = new SeckillOrderQuery();
        SeckillOrderQuery.Criteria criteria = seckillOrderQuery.createCriteria();

        String sellerId = seckillOrder.getSellerId();
        if (null!=sellerId&&!"".equals(sellerId)){
            criteria.andSellerIdEqualTo(sellerId);
            Page<SeckillOrder> page = (Page<SeckillOrder>) seckillOrderDao.selectByExample(seckillOrderQuery);
           return new PageResult(page.getTotal(),page.getResult());
        }
        Page<SeckillOrder> page = (Page<SeckillOrder>)seckillOrderDao.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }


    /**
     * 商品页面展示商品
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult searchY(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        seckillGoodsQuery.createCriteria().andStatusEqualTo("1");
        Page<SeckillGoods> seckillGoods = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);

        return new PageResult(seckillGoods.getTotal(),seckillGoods.getResult());
    }

    @Override
    public SeckillGoods findOne(Long id) {
        return seckillGoodsDao.selectByPrimaryKey(id);

    }
}

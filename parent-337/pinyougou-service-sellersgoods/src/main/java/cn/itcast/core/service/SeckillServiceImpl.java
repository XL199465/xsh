package cn.itcast.core.service;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.ithcast.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, String status) {
        PageHelper.startPage(pageNum, pageSize);
        //创建查询条件
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        Page<SeckillGoods> seckillGoods=null;
        //为零时查询全部
        if (status.equals("2")){
            // 开启分页
            seckillGoodsQuery.createCriteria().andStatusEqualTo("0");
             seckillGoods = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
            return new PageResult(seckillGoods.getTotal(),seckillGoods.getResult());
        }else if (status.equals("1")){
            seckillGoodsQuery.createCriteria().andStatusEqualTo("1");
            seckillGoods= (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
            return new PageResult(seckillGoods.getTotal(),seckillGoods.getResult());
        }
        //查询普通商品

        seckillGoods = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(null);
        return new PageResult(seckillGoods.getTotal(),seckillGoods.getResult());
    }
}

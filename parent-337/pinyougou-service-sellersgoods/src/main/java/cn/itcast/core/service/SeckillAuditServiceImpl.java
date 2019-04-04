package cn.itcast.core.service;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.ithcast.core.service.SeckillAuditService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import com.github.pagehelper.Page;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class SeckillAuditServiceImpl implements SeckillAuditService {
    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        seckillGoodsQuery.createCriteria().andStatusEqualTo("0");
        Page<SeckillGoods> seckillGoods = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
        return new PageResult(seckillGoods.getTotal(),seckillGoods.getResult());

    }

    /**
     * 修改订单申请
     * @param ids
     * @return
     */
    @Override
    public Result addAudit(Long[] ids) {
        if (ids.length>0){
            try {
            for (Long id : ids) {
                SeckillGoods seckillGoods = new SeckillGoods();
                seckillGoods.setId(id);
                seckillGoods.setStatus("1");
                seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);
                return new Result(true,"审核完成");
            }
            }catch (Exception e){
                e.printStackTrace();
                return new Result(false,"系统错误");
            }
        }
        return new Result(false,"没添加商品");
    }

}

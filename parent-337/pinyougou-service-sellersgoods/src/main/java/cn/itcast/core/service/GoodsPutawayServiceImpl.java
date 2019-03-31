package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.ithcast.core.service.GoodsPutawayService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@SuppressWarnings("all")
@Service
public class GoodsPutawayServiceImpl implements GoodsPutawayService {

    @Autowired
    private GoodsDao goodsDao;


    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Goods goods, String name) {
        PageHelper.startPage(pageNum, pageSize);
        GoodsQuery goodsQuery = new GoodsQuery();
        goodsQuery.createCriteria().andSellerIdEqualTo(name);
        String zt = goods.getAuditStatus();
        Page<Goods> good = null;
        if (zt.equals("0")) {
            good = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
            return new PageResult(good.getTotal(), good.getResult());
        } else if (zt.equals("1")) {
            goodsQuery.createCriteria().andIsMarketableEqualTo("1");
            good = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
            return new PageResult(good.getTotal(), good.getResult());
        } else {
            goodsQuery.createCriteria().andIsMarketableEqualTo("2");
            good = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
            return new PageResult(good.getTotal(), good.getResult());
        }
    }
    //引入监听器
    @Autowired
    private JmsTemplate jmsTemplate;
    // 注入主题
    @Autowired
    private Destination topicPageAndSolrDestination;
    //监听数据使用mq进行静态页面生成
    @Override
    public void putaway(Long[] selectIds) {

        if (selectIds.length > 0) {
            Goods goods = new Goods();
            goods.setIsMarketable("1");
            for (final Long selectId : selectIds) {
            goods.setId(selectId);
                goodsDao.updateByPrimaryKeySelective(goods);

                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(String.valueOf(selectId));
                    }
                });
            }

        }
    }
}

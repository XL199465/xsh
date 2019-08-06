package cn.ithcast.core.service;

import cn.ithcast.core.dao.good.GoodsDao;
import cn.ithcast.core.dao.item.ItemDao;
import cn.ithcast.core.pojo.good.Goods;
import cn.ithcast.core.pojo.good.GoodsQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
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
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        criteria.andSellerIdEqualTo(name).andAuditStatusEqualTo("1");
        String zt = goods.getAuditStatus();
        System.out.println(zt);

        if (zt.equals("0")) {
            Page<Goods>  good = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
            return new PageResult(good.getTotal(), good.getResult());
        } else if (zt.equals("1")) {
            criteria.andIsMarketableEqualTo(zt);
            Page<Goods> good = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
            return new PageResult(good.getTotal(), good.getResult());
        } else {
            criteria.andIsMarketableEqualTo(zt);
            Page<Goods> good = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
            return new PageResult(good.getTotal(), good.getResult());
        }
    }

    //引入监听器
    @Autowired
    private JmsTemplate jmsTemplate;
    //引入solr
    @Autowired
    private SolrTemplate solrTemplate;
    // 注入主题
    @Autowired
    private Destination topicPageAndSolrDestination;
    //引入dao
    @Autowired
    private ItemDao itemDao;
    //监听数据使用mq进行静态页面生成
    @Autowired
    private Destination queueSolrDeleteDestination;
    /*
     * 批量上架商品
     * */
    @Override
    public void putaway(Long[] selectIds) {

        if (selectIds.length > 0) {
            Goods goods = new Goods();
            goods.setIsMarketable("1");
            for (final Long selectId : selectIds) {
                goods.setId(selectId);
                goodsDao.updateByPrimaryKeySelective(goods);

                if (goods.getIsMarketable().equals("1")) {
                    //监听生成静态页面
                    //添加sorl索引
                    jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(String.valueOf(selectId));
                        }
                    });
//                    ItemQuery itemQuery = new ItemQuery();
//                    itemQuery.createCriteria().andGoodsIdEqualTo(goods.getId()).andIsDefaultEqualTo("0");
//                    List<Item> itemList = itemDao.selectByExample(itemQuery);
//                    solrTemplate.saveBeans(itemList, 1000);
                }
            }

        }
    }

    /*
     * 商品下架
     *
     * */
    @Override
    public Result sole_out(Long[] selectIds) {
        if (selectIds.length > 0) {
            for (final Long selectId : selectIds) {
                Goods goods = goodsDao.selectByPrimaryKey(selectId);
                if (goods.getIsMarketable().equals("0")) {
                    return new Result(false, "有商品未进行上架");
                } else {
                    //改变状态未下架
                    goods.setIsMarketable("2");
                    goodsDao.updateByPrimaryKeySelective(goods);
                    //删除静态页面

                    //删除sorl
                    jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {

                            return session.createTextMessage(String.valueOf(selectId));
                        }
                    });
                    return new Result(true,"下架成功");

                }
            }
        }
            return new Result(false, "没有选中商品");
    }

}

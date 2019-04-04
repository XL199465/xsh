package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojogroup.GoodsVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@SuppressWarnings("all")
public class GoodsServiceImpl implements GoodsService {

    // 注入GoodsDao对象
    @Autowired
    private GoodsDao goodsDao;

    // 注入GoodsDescDao对象
    @Autowired
    private GoodsDescDao goodsDescDao;

    // 注入ItemCatDao对象
    @Autowired
    private ItemCatDao itemCatDao;

    // 注入ItemDao对象
    @Autowired
    private ItemDao itemDao;

    // 注入BrandDao对象
    @Autowired
    private BrandDao brandDao;

    // 注入SellerDao对象
    @Autowired
    private SellerDao sellerDao;



    /*// 注入GoodsDetailService对象
    @Autowired
    private GoodsDetailService goodsDetailService;*/

    // 注入JMSTemplate对象
    @Autowired
    private JmsTemplate jmsTemplate;

    // 注入主题
    @Autowired
    private Destination topicPageAndSolrDestination;

    // 注入队列
    @Autowired
    private Destination queueSolrDeleteDestination;

    /**
     * 添加商品
     *
     * @param goodsVo
     */
    @Override
    public void add(GoodsVo goodsVo) {
//        保存商品
        // 设置商品状态为0
        goodsVo.getGoods().setAuditStatus("0");
        goodsVo.getGoods().setIsMarketable("1");
        // 保存并回显主键
        goodsDao.insertSelective(goodsVo.getGoods());

//        保存商品描述
        // 获取商品的id
        Long id = goodsVo.getGoods().getId();
        // 将商品id赋值给商品描述id
        goodsVo.getGoodsDesc().setGoodsId(id);
        // 保存
        goodsDescDao.insertSelective(goodsVo.getGoodsDesc());

        // 保存库存
        // 首先判断是否启用规格
        if ("1".equals(goodsVo.getGoods().getIsEnableSpec())) {
            List<Item> itemList = goodsVo.getItemList();

            for (Item item : itemList) {
                // 库存标题 商品名称+规格名称
                String goodsName = goodsVo.getGoods().getGoodsName();

                // 获取规格{机身内存:"", 颜色:""};
                String spec = item.getSpec();
                // 将json格式的字符串转换成为Map集合 {机身内存="", 颜色=""}
                Map specMap = JSON.parseObject(spec, Map.class);
                // 遍历map集合,并拼接标题
                Set<Map.Entry<String, String>> set = specMap.entrySet();
                for (Map.Entry<String, String> entry : set) {
                    goodsName += " " + entry.getValue();
                }
                // 将拼接之后的结果封装到item对象中
                item.setTitle(goodsName);

                //图片  从商品表一堆图片中 第一张
                String itemImages = goodsVo.getGoodsDesc().getItemImages();
                // 将实体图片转换成为数组
                List<Map> imageMap = JSON.parseArray(itemImages, Map.class);
                // 获取第一张照片的URL路径并赋值给SKU对象
                if (null != imageMap && imageMap.size() > 0) {
                    String imageURL = (String) imageMap.get(0).get("url");
                    item.setImage(imageURL);
                }

                // 商品三级分类的id
                Long category3Id = goodsVo.getGoods().getCategory3Id();
                item.setCategoryid(category3Id);

                // 添加时间和更新时间
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());

                // 商品id
                item.setGoodsId(goodsVo.getGoods().getId());

                // 商家id
                item.setSellerId(goodsVo.getGoods().getSellerId());

                // 商品三级分类的名称  需要根据三级分类的id去查询
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(category3Id);
                String itemCatName = itemCat.getName();
                item.setCategory(itemCatName);

                // 设置品牌名称  需要根据品牌id查询
                Brand brand = brandDao.selectByPrimaryKey(goodsVo.getGoods().getBrandId());
                String brandName = brand.getName();
                item.setBrand(brandName);

                // 设置卖家的别名
                Seller seller = sellerDao.selectByPrimaryKey(goodsVo.getGoods().getSellerId());
                String sellerNickName = seller.getNickName();
                item.setSeller(sellerNickName);

                // 保存数据
                itemDao.insertSelective(item);
            }
        } else {
            // do nothing
        }
    }

    /**
     * 分页+条件查询商品
     *
     * @param pageNum
     * @param pageSize
     * @param goods
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Goods goods) {
        PageHelper.startPage(pageNum, pageSize);
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();


        //审核状态
        if (null != goods.getAuditStatus() && !"".equals(goods.getAuditStatus())) {
            criteria.andAuditStatusEqualTo(goods.getAuditStatus());
        }
        //商品名称
        if (null != goods.getGoodsName() && !"".equals(goods.getGoodsName().trim())) {
            criteria.andGoodsNameLike("%" + goods.getGoodsName().trim() + "%");
        }
        //只显示不删除的商品   是null才是不删除的   不是null是删除了的商品
        criteria.andIsDeleteIsNull();

        //只能查询当前登陆人 他家的商品不允许查询
        if (null != goods.getSellerId()) {
            criteria.andSellerIdEqualTo(goods.getSellerId());
        }

        //查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);

        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 根据商品id查询
     *
     * @param id
     * @return
     */
    @Override
    public GoodsVo findOne(Long id) {
        // 创建包装类对象
        GoodsVo goodsVo = new GoodsVo();

        // 封装GoodsVo对象
        // 根据商品id查询商品,并封装到包装类中
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsVo.setGoods(goods);

        // 根据商品id查询商品详情表,并封装到包装类中
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsVo.setGoodsDesc(goodsDesc);

        // 条件查询 根据商品id条件查询SKU列表
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        goodsVo.setItemList(itemList);

        // 返回结果
        return goodsVo;
    }

    /**
     * 修改商品
     *
     * @param goodsVo
     */
    @Override
    public void update(GoodsVo goodsVo) {
        // 修改商品对象
        Goods goods = goodsVo.getGoods();
        goodsDao.updateByPrimaryKeySelective(goods);

        // 修改商品详情对象
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);

        // 修改SKU列表
        // 1.首先删除所有的原有记录
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(itemQuery);

        // 2.添加新的记录
        if ("1".equals(goodsVo.getGoods().getIsEnableSpec())) {
            List<Item> itemList = goodsVo.getItemList();
            for (Item item : itemList) {
                // 设置标题
                String goodsName = goodsVo.getGoods().getGoodsName();

                String spec = item.getSpec();
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                Set<Map.Entry<String, String>> set = map.entrySet();
                for (Map.Entry<String, String> entry : set) {
                    goodsName += " " + entry.getValue();
                }

                item.setTitle(goodsName);

                // 设置图片
                String itemImages = goodsVo.getGoodsDesc().getItemImages();
                List<Map> imageList = JSON.parseArray(itemImages, Map.class);
                if (null != imageList && imageList.size() > 0) {
                    String url = (String) imageList.get(0).get("url");
                    item.setImage(url);
                }

                // 设置三级分类的id
                Long category3Id = goodsVo.getGoods().getCategory3Id();
                item.setCategoryid(category3Id);

                // 设置修改和创建时间
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());

                // 设置商品id
                item.setGoodsId(goodsVo.getGoods().getId());

                // 设置商家id
                item.setSellerId(goodsVo.getGoods().getSellerId());

                // 设置品牌名称
                Brand brand = brandDao.selectByPrimaryKey(goodsVo.getGoods().getBrandId());
                String brandName = brand.getName();
                item.setBrand(brandName);

                // 设置三级分类的名称
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(goodsVo.getGoods().getCategory3Id());
                String itemCatName = itemCat.getName();
                item.setCategory(itemCatName);

                // 设置商家名称
                Seller seller = sellerDao.selectByPrimaryKey(goodsVo.getGoods().getSellerId());
                String sellerNickName = seller.getNickName();
                item.setSeller(sellerNickName);

                // 保存商品
                itemDao.insertSelective(item);
            }
        } else {
            // do nothing
        }
    }

    /**
     * 运营商批量修改商品状态
     *
     * @param selectIds
     * @param id
     */
    @Override
    public void updateStatus(Long[] selectIds, String id) {
        Goods goods = new Goods();
        goods.setAuditStatus(id);

        if (null != selectIds) {
            for (final Long selectId : selectIds) {
                goods.setId(selectId);
                // 1.更新状态
                goodsDao.updateByPrimaryKeySelective(goods);

                // TODO 存入索引库
                // 判断是否为审核通过,当id为1代表审核通过
                if ("1".equals(id)) {
                    // 只有审核通过的商品才会更新索引库
                   /* ItemQuery itemQuery = new ItemQuery();
                    ItemQuery.Criteria criteria = itemQuery.createCriteria();
                    criteria.andGoodsIdEqualTo(selectId).andIsDefaultEqualTo("1");
                    List<Item> itemList = itemDao.selectByExample(itemQuery);
                    solrTemplate.saveBeans(itemList, 1000);
*/
                    // 商品详情页面的静态化处理
                    /*goodsDetailService.showGoodsDetail(selectId);*/

                    // 向ActiveMQ发送消息 会被search和page分别接收 主题模式
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

    /**
     * 运营商批量删除商品
     *
     * @param selectIds
     */
    @Override
    public void del(Long[] selectIds) {
        Goods goods = new Goods();
        goods.setIsDelete("1");

        if (null != selectIds) {
            for (final Long selectId : selectIds) {
                goods.setId(selectId);
                goodsDao.updateByPrimaryKeySelective(goods);

                // 存入索引库
                /*SolrDataQuery solrDataQuery = new SimpleQuery();
                solrDataQuery.addCriteria(new Criteria("item_goodsid").is(selectId));
                solrTemplate.delete(solrDataQuery);
                solrTemplate.commit();*/

                // 向ActiveMQ发送消息,被search接收
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(String.valueOf(selectId));
                    }
                });
            }
        }
    }

    //引入秒杀表
    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    /**
     * 添加到秒杀
     *
     * @param ids
     * @return
     */
    @Override
    public Result addSeconds(Long[] ids, SeckillGoods seckillGoods) {
        if (ids.length > 0) {
            for (Long id : ids) {
                ItemQuery itemQuery = new ItemQuery();
                itemQuery.createCriteria().andGoodsIdEqualTo(id);
                List<Item> itemList = itemDao.selectByExample(itemQuery);
                for (Item item : itemList) {

                    seckillGoods.setItemId(item.getId());
                    seckillGoods.setGoodsId(item.getGoodsId());
                    seckillGoods.setSellerId(item.getSellerId());
                    seckillGoods.setTitle(item.getTitle());
                    seckillGoods.setSmallPic(item.getImage());
                    seckillGoods.setPrice(item.getPrice());
                    seckillGoods.setCreateTime(new Date());
                    //添加未审核
                    seckillGoods.setStatus("0");
                    seckillGoods.setNum(item.getNum());
                    seckillGoods.setIntroduction(item.getSellPoint());

                    seckillGoodsDao.insertSelective(seckillGoods);

                }
            }
            return new Result(true,"添加秒杀成功");
        }
        return new Result(false, "没有选中数据");
    }
}

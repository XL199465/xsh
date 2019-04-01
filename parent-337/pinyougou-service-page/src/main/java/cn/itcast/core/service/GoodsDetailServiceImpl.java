package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.ithcast.core.service.GoodsDetailService;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsDetailServiceImpl implements GoodsDetailService, ServletContextAware {

    // 注入FreeMarkerConfigurer对象
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    // 注入ItemDao对象
    @Autowired
    private ItemDao itemDao;

    // 注入GoodsDescDao对象
    @Autowired
    private GoodsDescDao goodsDescDao;

    // 注入GoodsDao对象
    @Autowired
    private GoodsDao goodsDao;

    // 注入ItemCatDao对象
    @Autowired
    private ItemCatDao itemCatDao;

    /**
     * 展示商品详情
     *
     * @param id
     */
    @Override
    public void showGoodsDetail(Long id) {

        // 获取Configuration对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();

        // 创建输出流对象
        Writer out = null;

        // 获取页面的绝对路径
        String path = getPath("/" + id + ".html");

        try {
            // 获取模板对象
            Template template = configuration.getTemplate("item.ftl");

            // 创建数据
            Map<String, Object> root = new HashMap<>();

            // 添加SKU库存数据
            ItemQuery itemQuery = new ItemQuery();
            ItemQuery.Criteria criteria = itemQuery.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            root.put("itemList", itemList);

            // 添加商品详情信息
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
            root.put("goodsDesc", goodsDesc);

            // 添加商品信息
            Goods goods = goodsDao.selectByPrimaryKey(id);
            root.put("goods", goods);

            // 添加分类名称信息
            root.put("itemCat1", itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName());
            root.put("itemCat2", itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName());
            root.put("itemCat3", itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());

            // 创建输出流
            out = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");

            // 执行
            template.process(root,out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 提供获取绝对路径的方法
    private String getPath(String fileName) {
        return servletContext.getRealPath(fileName);
    }

    // 通过成员变量和set方法注入ServletContext对象那个
    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

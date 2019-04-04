package cn.ithcast.core.service;

import cn.ithcast.core.dao.ad.ContentDao;
import cn.ithcast.core.pojo.ad.Content;
import cn.ithcast.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;

    // 注入RedisTemplate对象
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Content> findAll() {
        List<Content> list = contentDao.selectByExample(null);
        return list;
    }

    @Override
    public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Content> page = (Page<Content>) contentDao.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(Content content) {
        // 在真正的修改添加之前,清空缓存
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());

        contentDao.insertSelective(content);
    }

    @Override
    public void edit(Content content) {
        // 隐藏属性,广告的id
        Content preContent = contentDao.selectByPrimaryKey(content.getId());

        // 判断两者的id是否相等
        if (!preContent.getCategoryId().equals(content.getCategoryId())) {
            // 说明修改了广告分类id  清空之前的缓存
            redisTemplate.boundHashOps("content").delete(preContent.getCategoryId());
        }

        // 广告分类id没发生变化  只需要清空当前的缓存了
        Long contentCategoryId = content.getCategoryId();
        redisTemplate.boundHashOps("content").delete(contentCategoryId);

        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public Content findOne(Long id) {
        Content content = contentDao.selectByPrimaryKey(id);
        return content;
    }

    @Override
    public void delAll(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                // 删除缓存
                Content content = contentDao.selectByPrimaryKey(id);
                redisTemplate.boundHashOps("content").delete(content.getCategoryId());


                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    //根据广告分类ID 查询此分类下所有广告集合
    @Override
    public List<Content> findByCategoryId(Long categoryId) {

        // 1.从缓存中获取
        List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);

        // 判断
        if (null == contentList || contentList.size() == 0) {
            // 说明是空的,那么就从数据库中查询
            ContentQuery contentQuery = new ContentQuery();
            // 添加条件
            contentQuery.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
            contentQuery.setOrderByClause("sort_order desc");

            // 从数据库中查询
            contentList = contentDao.selectByExample(contentQuery);

            // 将结果存入redis中
            redisTemplate.boundHashOps("content").put(categoryId, contentList);

            // 设置缓存时间
            redisTemplate.boundHashOps("content").expire(8, TimeUnit.HOURS);
        }

        // 最后返回contentList
        return contentList;
    }
}

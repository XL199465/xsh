package cn.ithcast.core.service;

import cn.ithcast.core.dao.item.ItemCatDao;
import cn.ithcast.core.pojo.item.ItemCat;
import cn.ithcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    // 注入ItemCatDao对象
    @Autowired
    private ItemCatDao itemCatDao;

    // 注入RedisTemplate对象
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ItemCat> findByParentId(Long parentId) {

        // 查询所有商品分类并存入缓存中
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        for (ItemCat itemCat : itemCats) {
            redisTemplate.boundHashOps("itemCatList").put(itemCat.getName(), itemCat.getTypeId());
        }


        // 创建条件查询对象
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();

        // 添加条件查询
        criteria.andParentIdEqualTo(parentId);

        // 执行方法并返回结果
        return itemCatDao.selectByExample(itemCatQuery);
    }

    /**
     * 添加商品分类
     *
     * @param itemCat
     */
    @Override
    public void add(ItemCat itemCat) {
        itemCatDao.insertSelective(itemCat);
    }

    /**
     * 修改商品分类之数据回显
     *
     * @param id
     * @return
     */
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    /**
     * 修改数据
     *
     * @param itemCat
     */
    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    /**
     * 删除商品分类
     *
     * @param selectIds
     */
    @Override
    public Result del(Long[] selectIds) {

        // 创建条件查询对象
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();

        // 首先对数组非空判断
        if (null != selectIds) {
            // 遍历数组
            for (Long id : selectIds) {
                criteria.andParentIdEqualTo(id);
                if (itemCatDao.selectByExample(itemCatQuery).size() > 0) {
                    return new Result(false, "你选择删除的商品分类存在子分类");
                } else {
                    itemCatDao.deleteByPrimaryKey(id);
                    return new Result(true, "删除商品分类成功");
                }
            }
        }
        return null;
    }

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }
}

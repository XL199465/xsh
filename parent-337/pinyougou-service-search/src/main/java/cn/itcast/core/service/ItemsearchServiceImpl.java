package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.ithcast.core.service.ItemsearchService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.math.BigDecimal;
import java.util.*;

@Service
@SuppressWarnings("all")
public class ItemsearchServiceImpl implements ItemsearchService {

    // 因为是从索引库中查询,所以注入SpringDataSolr对象
    @Autowired
    private SolrTemplate solrTemplate;

    // 注入缓存对象RedisTemplate
    @Autowired
    private RedisTemplate redisTemplate;

    //注入itemDao对象
    @Autowired
    private ItemDao itemDao;


    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        // 对传递过来的关键字预处理
        String beforeKeywords = searchMap.get("keywords");
        String afterKeywords = beforeKeywords.replace(" ", "");
        searchMap.put("keywords", afterKeywords);

        // 创建返回的结果集Map
        Map<String, Object> resultMap = new HashMap<>();

        // 添加去重后的商品分类展示
        List<String> categoryList = showCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);

        // 从缓存中获取品牌和规格数据
        Map<String, Object> brandAndSpecListMap = searchBrandListAndSpecListByCategory(categoryList.get(0));
        resultMap.putAll(brandAndSpecListMap);

        // 添加分页+高亮+关键字查询功能
        Map<String, Object> pageMap = searchByPageWithHighLight(searchMap);
        resultMap.putAll(pageMap);

        // 返回结果集
        return resultMap;
    }

    @Override
    public void addToCollect(Integer itemId) {
        redisTemplate.boundValueOps("itemId").set(itemId);
    }

    @Override
    public Item findAll() {
        Integer itemId = (Integer) redisTemplate.boundValueOps("itemId").get();

        return itemDao.selectByPrimaryKey(Long.valueOf(itemId));
    }


    // 从缓存中获取品牌和规格分类
    private Map<String, Object> searchBrandListAndSpecListByCategory(String category) {
        // 自定义Map集合
        Map<String, Object> resultMap = new HashMap<>();

        // 从缓存中获取模板id
        Object typeId = redisTemplate.boundHashOps("itemCatList").get(category);

        // 再根据模板id从缓存中获取品牌集
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);


        // 再根据模板id从缓存中获取规格集
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);

        // 然后将结果存入结果集中
        resultMap.put("brandList", brandList);
        resultMap.put("specList", specList);

        return resultMap;
    }


    // 去重后的商品分类展示
    private List<String> showCategoryList(Map<String, String> searchMap) {
        // 获取关键字
        String keywords = searchMap.get("keywords");

        // 创建条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);

        // 创建查询对象
        Query query = new SimpleQuery(criteria);

        // 设置分组 目的是去重
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        // 获取分组分页结果集
        GroupPage<Item> page = solrTemplate.queryForGroupPage(query, Item.class);

        // 获取分类名称,并添加到自定义集合中
        // 自定义集合
        List<String> categoryList = new ArrayList<>();

        GroupResult<Item> itemGroupResult = page.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = itemGroupResult.getGroupEntries();
        List<GroupEntry<Item>> groupEntriesContent = groupEntries.getContent();
        // 非空判断
        if (null != groupEntriesContent && groupEntriesContent.size() > 0) {
            for (GroupEntry<Item> itemGroupEntry : groupEntriesContent) {
                categoryList.add(itemGroupEntry.getGroupValue());
            }
        }

        // 返回结果集
        return categoryList;
    }

    // 分页+高亮+关键字查询功能
    private Map<String, Object> searchByPageWithHighLight(Map<String, String> searchMap) {
        // 创建Map集合
        Map<String, Object> resultMap = new HashMap<>();

        // 获取关键字
        String keywords = searchMap.get("keywords");

        // 创建条件对象,并添加条件
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(keywords);

        // 创建高亮查询对象
        HighlightQuery highlightQuery = new SimpleHighlightQuery(criteria);

        // 添加分页查询
        Integer pageNo = Integer.parseInt(searchMap.get("pageNo"));
        Integer pageSize = Integer.parseInt(searchMap.get("pageSize"));
        highlightQuery.setOffset((pageNo - 1) * pageSize);
        highlightQuery.setRows(pageSize);

        // 设置高亮查询的参数(不需要开启高亮,因为一旦创建查询对象时,就开启了)
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        highlightQuery.setHighlightOptions(highlightOptions);

        // 商品分类的过滤查询
        if (!"".equals(searchMap.get("category"))) {
            // 创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();

            // 创建条件对象,并添加条件
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            highlightQuery.addFilterQuery(filterQuery);
        }

        // 商品品牌的过滤查询
        if (!"".equals(searchMap.get("brand"))) {
            // 创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();

            // 创建条件对象,并添加条件
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            highlightQuery.addFilterQuery(filterQuery);
        }

        // 商品规格的过滤查询 key:规格名称 value:规格选项名称
        if (searchMap.get("spec") != null) {
            String spec = searchMap.get("spec");
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            Set<String> strings = map.keySet();
            for (String string : strings) {
                FilterQuery filterQuery = new SimpleFacetQuery();

                // 创建条件对象,并添加条件 动态域查询
                Criteria filterCriteria = new Criteria("item_spec_" + string).is(map.get(string));
                filterQuery.addCriteria(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }

        // 商品价格的过滤查询
        if (!"".equals(searchMap.get("price"))) {
            FilterQuery filterQuery = new SimpleFacetQuery();
            String price = searchMap.get("price");
            String[] split = price.split("-");

            if (!split[0].equals("0")) {
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(split[0]);
                filterQuery.addCriteria(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }

            if (!split[1].equals("*")) {
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(split[1]);
                filterQuery.addCriteria(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }

        // 更新时间的排序
        if (null != searchMap.get("sort") && !"".equals(searchMap.get("sort"))) {
            Sort sort = null;
            if ("ASC".equals(searchMap.get("sort"))) {
                sort = new Sort(Sort.Direction.ASC, "item_" + searchMap.get("sortField"));
            } else {
                sort = new Sort(Sort.Direction.DESC, "item_" + searchMap.get("sortField"));

            }
            highlightQuery.addSort(sort);
        }


        // 分页+高亮查询
        HighlightPage<Item> page = solrTemplate.queryForHighlightPage(highlightQuery, Item.class);

        // 获取高亮实例对象集合
        List<HighlightEntry<Item>> highlightEntryList = page.getHighlighted();
        for (HighlightEntry<Item> itemHighlightEntry : highlightEntryList) {
            Item entity = itemHighlightEntry.getEntity();

            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            // 非空判断
            if (null != highlights && highlights.size() > 0) {
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }

        // 获取总条数 并存入自定义集合中
        long totalElements = page.getTotalElements();
        resultMap.put("total", totalElements);

        // 获取总页数,并存入自定义集合中
        int totalPages = page.getTotalPages();
        resultMap.put("totalPages", totalPages);


        // 获取结果集对象 将结果集存入自定义集合中
        List<Item> itemList = page.getContent();
        resultMap.put("rows", itemList);

        return resultMap;
    }


    // 分页+关键字查询功能
    private Map<String, Object> searchByPage1(Map<String, String> searchMap) {
        // 创建Map集合
        Map<String, Object> resultMap = new HashMap<>();

        // 获取关键字
        String keywords = searchMap.get("keywords");

        // 创建条件对象,并添加条件
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(keywords);

        // 创建查询对象
        Query query = new SimpleQuery(criteria);

        // 添加分页查询
        Integer pageNo = Integer.parseInt(searchMap.get("pageNo"));
        Integer pageSize = Integer.parseInt(searchMap.get("pageSize"));
        query.setOffset((pageNo - 1) * pageSize);
        query.setRows(pageSize);

        // 分页查询
        ScoredPage<Item> page = solrTemplate.queryForPage(query, Item.class);

        // 获取总条数 并存入自定义集合中
        long totalElements = page.getTotalElements();
        resultMap.put("total", totalElements);

        // 获取总页数,并存入自定义集合中
        int totalPages = page.getTotalPages();
        resultMap.put("totalPages", totalPages);


        // 获取结果集对象 将结果集存入自定义集合中
        List<Item> itemList = page.getContent();
        resultMap.put("rows", itemList);

        return resultMap;
    }
}

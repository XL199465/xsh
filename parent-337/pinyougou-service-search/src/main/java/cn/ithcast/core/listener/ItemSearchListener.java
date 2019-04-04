package cn.ithcast.core.listener;

import cn.ithcast.core.dao.item.ItemDao;
import cn.ithcast.core.pojo.item.Item;
import cn.ithcast.core.pojo.item.ItemQuery;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;

public class ItemSearchListener implements MessageListener {

    // 注入ItemDao对象
    @Autowired
    private ItemDao itemDao;

    // 注入SolrTemplate对象
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
        try {
            // 打印获取到的内容
            String id = activeMQTextMessage.getText();
            System.out.println("搜索服务在更新索引库时接收到的数据:" + id);

            // 执行更新索引库的动作
            ItemQuery itemQuery = new ItemQuery();
            itemQuery.createCriteria().andGoodsIdEqualTo(Long.parseLong(id)).andIsDefaultEqualTo("1");
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            solrTemplate.saveBeans(itemList, 1000);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

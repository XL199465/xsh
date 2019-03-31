package cn.itcast.core.listener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class ItemDeleteListener implements MessageListener{

    // 注入SolrTemplate对象
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage)message;
        try {
            String id = activeMQTextMessage.getText();
            System.out.println("搜索服务在删除索引库时接收到的数据:" + id);

            // 删除索引库
            SolrDataQuery solrDataQuery = new SimpleQuery();
            solrDataQuery.addCriteria(new Criteria("item_goodsid").is(id));
            solrTemplate.delete(solrDataQuery);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

package cn.ithcast.core.listener;

import cn.ithcast.core.service.Sold_outService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
@SuppressWarnings("all")
public class sold_outListener implements MessageListener{

    // 注入SolrTemplate对象
    @Autowired
    private SolrTemplate solrTemplate;
    //注入删除静态页面的方法
    @Autowired
    private Sold_outService sold_outService;
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
            //删除静态页面
            sold_outService.DeletePage(id);


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


}

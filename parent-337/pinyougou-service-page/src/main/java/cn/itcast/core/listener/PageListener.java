package cn.itcast.core.listener;

import cn.ithcast.core.service.GoodsDetailService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class PageListener implements MessageListener {

    // 注入GoodsDetailService对象
    @Autowired
    private GoodsDetailService goodsDetailService;
    //注入sorl对象
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
        try {
            String id = activeMQTextMessage.getText();
            System.out.println("使用静态化技术生成页面时获取到的id:" + id);

            // 商品详情页面的静态化处理
            goodsDetailService.showGoodsDetail(Long.parseLong(id));



        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

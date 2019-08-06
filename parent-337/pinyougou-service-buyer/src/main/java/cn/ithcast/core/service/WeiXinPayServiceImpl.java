package cn.ithcast.core.service;

import cn.ithcast.core.common.utils.HttpClient;
import cn.ithcast.core.dao.order.OrderDao;
import cn.ithcast.core.pojo.log.PayLog;
import cn.ithcast.core.pojo.order.Order;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    // 注入RedisTemplate对象
    @Autowired
    private RedisTemplate redisTemplate;

    // 注入OrderDao对象
    @Autowired
    private OrderDao orderDao;

    /**
     * 生成二维码
     *
     * @return
     */
    public Map createNative(String name) {
        // 从缓存中获取支付日志对象
        PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(name);

        //1.创建参数
        Map<String, String> param = new HashMap();//创建参数
        param.put("appid", appid);//公众号
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("sign", WXPayUtil.generateUUID());
        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", payLog.getOutTradeNo());//商户订单号
        // param.put("total_fee", String.valueOf(payLog.getTotalFee()));//总金额（分）
        param.put("total_fee", "1");
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", "http://www.itcast.cn");//回调地址(随便写)
        param.put("trade_type", "NATIVE");//交易类型
        try {
            //2.生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println(xmlParam);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //3.获得结果
            String result = client.getContent();
            System.out.println(result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String, String> map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("total_fee", payLog.getTotalFee().toString());//总金额
            map.put("out_trade_no", payLog.getOutTradeNo());//订单号
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no, String name) {
        // 封装入参
        Map<String, String> param = new HashMap<>();
        // 公众账号ID	appid  是
        param.put("appid", appid);
        // 商户号	mch_id	是
        param.put("mch_id", partner);
        // 微信订单号	transaction_id	二选一	String(32)	1009660380201506130728806387	微信的订单号，建议优先使用
        // 商户订单号	out_trade_no	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 详见商户订单号
        param.put("out_trade_no", out_trade_no);
        // 随机字符串	nonce_str	是
        param.put("nonce_str", WXPayUtil.generateNonceStr());

        try {
            // 签名	sign	是
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            // 创建httpclient对象,并设置属性
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            // 发送请求
            httpClient.post();
            // 获取结果
            String content = httpClient.getContent();
            // 将结果转换成为map集合并返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            // 判断是否支付成功
            if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                // 如果支付成功,更改支付日志的数据
                updatePayLog(resultMap.get("transaction_id"), name);
            }
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 订单超时 关闭微信支付的接口
     *
     * */
    @Override
    public void CloseInterface(String out_trade_no) {
        Map<String, String> close = new HashMap<>();
        // 公众账号ID	appid  是
        close.put("appid", appid);
        // 商户号	mch_id	是
        close.put("mch_id", partner);
        // 微信订单号	transaction_id	二选一	String( 11132)	1009660380201506130728806387	微信的订单号，建议优先使用
        // 商户订单号	out_trade_no	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 详见商户订单号
        close.put("out_trade_no", out_trade_no);
        // 随机字符串	nonce_str	是
        close.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            //查询签名
            String signedXml = WXPayUtil.generateSignedXml(close, partnerkey);
            //发送请求                                   https://api.mch.weixin.qq.com/secapi/pay/reverse
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/reverse");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            // 发送请求
            httpClient.post();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 更新支付日志的数据
     *
     * @param transaction_id
     * @param name
     */
    private void updatePayLog(String transaction_id, String name) {
        // 从缓存中获取支付日志对象
        PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(name);

        // 1.修改支付日志的状态
        // 支付时间
        payLog.setPayTime(new Date());
        // 流水号
        payLog.setTransactionId(transaction_id);
        // 支付状态
        payLog.setTradeState("1");

        // 2.更改关联的订单表订单的状态
        String orderList = payLog.getOrderList();
        String[] orders = orderList.split(",");
        Order order = new Order();
        // 状态
        order.setStatus("2");
        // 更新时间
        order.setUpdateTime(new Date());
        // 支付时间
        order.setPaymentTime(new Date());
        // 订单号
        for (String orderId : orders) {
            order.setOrderId(Long.parseLong(orderId));
            orderDao.updateByPrimaryKeySelective(order);
        }

        // 3.清空缓存中的支付日志
        redisTemplate.boundHashOps("payLog").delete(name);

    }


}


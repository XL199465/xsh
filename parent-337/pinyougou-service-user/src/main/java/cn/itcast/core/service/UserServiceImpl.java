package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.ithcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    // 注入JmsTemplate对象
    @Autowired
    private JmsTemplate jmsTemplate;

    // 注入Destination对象
    @Autowired
    private Destination smsDestination;

    // 注入JedisTemplate对象
    @Autowired
    private RedisTemplate redisTemplate;

    // 注入UserDao对象
    @Autowired
    private UserDao userDao;

    @Override
    public void sendCode(final String phone) {
        // 生成一个随机的六位验证码
        final String templateParam = RandomStringUtils.randomNumeric(6);
        System.out.println("六位验证码:" + templateParam);

        // 将验证码保存到缓存中,为了注册时验证验证码是否填写正确
        redisTemplate.boundValueOps(phone).set(templateParam);
        // 正常情况下,需要设置过期时间
        // redisTemplate.boundValueOps(phone).expire(1, TimeUnit.MINUTES);

        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage smsMap = session.createMapMessage();
                // 设置手机号码
                smsMap.setString("phoneNumbers", phone);
                // 设置签名
                smsMap.setString("signName", "品品优购物");
                smsMap.setString("templateCode", "SMS_161325315");
                smsMap.setString("templateParam", "{'number':'" + templateParam + "'}");

                return smsMap;
            }
        });
    }

    /**
     * 注册
     *
     * @param smscode
     * @param user
     */
    @Override
    public void add(String smscode, User user) {
        // 首先判断验证码是否失效
        String vetifyCode = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if (null == vetifyCode) {
            throw new RuntimeException("验证码已失效");
        }

        // 再判断验证码是否一致
        if (vetifyCode.equals(smscode)) {
            // 保存用户
            user.setCreated(new Date());
            user.setUpdated(new Date());

            // 加密
          /*  String password = DigestUtils.md5Hex(smscode);
            user.setPassword(password);*/

            userDao.insertSelective(user);
        } else {
            throw new RuntimeException("验证码有误");
        }

    }
}

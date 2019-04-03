package cn.itcast.core.service;

import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.log.PayLogQuery;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import cn.itcast.core.pojogroup.Orderpp;
import cn.itcast.core.pojogroup.UserVo;
import cn.ithcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import redis.clients.jedis.Jedis;

import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
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

    // 注入PayLogDao对象
    @Autowired
    private PayLogDao payLogDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;




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

    /**
     * 分页+条件查询
     *
     * @param pageNum
     * @param pageSize
     * @param user
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, User user) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 创建条件查询对象
        UserQuery userQuery = new UserQuery();
        UserQuery.Criteria criteria = userQuery.createCriteria();

        // 添加查询条件
        if (null != user.getUsername() && !"".equals(user.getUsername())) {
            criteria.andUsernameLike("%" + user.getUsername() + "%");
        }

        // 执行方法
        Page<User> page = (Page<User>) userDao.selectByExample(userQuery);

        // 返回结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 冻结用户
     *
     * @param selectIds
     */
    @Override
    public void updateStatus(Long[] selectIds) {
        User user = new User();
        user.setStatus("0");

        // 修改状态
        if (null != selectIds) {
            for (Long selectId : selectIds) {
                user.setId(selectId);
                userDao.updateByPrimaryKeySelective(user);
            }
        }
    }

    /**
     * 根据用户名查询用户
     *
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        UserQuery userQuery = new UserQuery();
        UserQuery.Criteria criteria = userQuery.createCriteria();

        criteria.andUsernameEqualTo(username);
        List<User> userList = userDao.selectByExample(userQuery);

        if (null != userList && userList.size() > 0) {
            return userList.get(0);
        }
        return null;
    }

    /**
     * 用户活跃度统计
     *
     * @return
     */
    @Override
    public UserVo countActivity() {
        UserVo userVo = new UserVo();

        // 用户总数
        Integer userTotalCount = userDao.queryAll();
        userVo.setUserTotalCount(userTotalCount);

        // 活跃用户 (最近七天至少登录一次为活跃用户)
        List<User> userList = userDao.selectByExample(null);
        Integer activityUserCount = 0;
        for (User user : userList) {
            if (null != user.getLastLoginTime() && (System.currentTimeMillis() - user.getLastLoginTime().getTime()) < 7 * 24 * 3600 * 1000) {
                activityUserCount++;
            }
        }
        userVo.setActivityUserCount(activityUserCount);

        // 非活跃用户
        userVo.setUnactivityUserCount(userTotalCount - activityUserCount);

        System.out.println(userVo.getUserTotalCount()+userVo.getActivityUserCount()+userVo.getUnactivityUserCount());

        // 返回结果
        return userVo;
    }


    //查询我的订单
    @Override
    public List<Orderpp> findAllOrders(String name) {
        List<Orderpp> orderppList = new ArrayList<>();

        //根据用户名查询订单
        PayLogQuery query = new PayLogQuery();
        query.createCriteria().andUserIdEqualTo(name);
        //获取该用户多个订单
        List<PayLog> logList = payLogDao.selectByExample(query);

        for (PayLog payLog : logList) {
            String orderList = payLog.getOrderList();
            String[] orderList_Order_Id = orderList.split(",");
            //根据订单号查询order表
            for (String s : orderList_Order_Id) {
                Orderpp orderpp = new Orderpp();
                Order order = new Order();

                order = orderDao.selectByPrimaryKey(Long.parseLong(s.trim()));
                orderpp.setOrder(order);
                //根据order表order-id查询商品结果集
                OrderItemQuery orderItemQuery = new OrderItemQuery();
                orderItemQuery.createCriteria().andOrderIdEqualTo(order.getOrderId());
                List<OrderItem> orderItemList1 = orderItemDao.selectByExample(orderItemQuery);
                List<OrderItem> orderItemList = new ArrayList<>();

                for (OrderItem item : orderItemList1) {
                    orderItemList.add(item);
                }

                orderpp.setOrderitemList(orderItemList);
                orderppList.add(orderpp);
            }
        }
        return orderppList;
    }


}

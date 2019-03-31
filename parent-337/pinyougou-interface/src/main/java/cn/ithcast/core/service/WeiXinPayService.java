package cn.ithcast.core.service;

import java.util.Map;

public interface WeiXinPayService {

    /**
     * 生成二维码
     */
    Map<String, Object> createNative(String name);

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryPayStatus(String out_trade_no, String name);
}

package cn.itcast.core.controller;

import cn.itcast.core.common.utils.IdWorker;
import cn.ithcast.core.service.WeiXinPayService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("pay")
@SuppressWarnings("all")
public class PayController {

    // 注入WeiXinService对象
    @Reference
    private WeiXinPayService weiXinPayService;

    /**
     * 生成二维码
     */
    @RequestMapping("/createNative")
    public Map<String, Object> createNative() {
        // 获取当前联系人,并作为入参传递到service层
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return weiXinPayService.createNative(name);
    }

    /**
     * 查询支付状态
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {

        // 获取当前登录人姓名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        // 定义对象result
        Result result = null;

        // 设置变量,用来跳出循环,当用户一直不付款,就要使用这个变量,当变量累加到一定值时,跳出循环
        int temp = 0;

        // 不停的询问
        while (true) {
            // 询问微信
            Map<String, String> map = weiXinPayService.queryPayStatus(out_trade_no, name);
            // 判断
            if (map == null) {
                result = new Result(false, "支付失败");
                break;
            }
            if ("SUCCESS".equals(map.get("trade_state"))) {
                result = new Result(true, "支付成功");
                break;
            }
            // 然后间隔几秒,再次询问
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            temp++;
            if (temp >= 2) {
                weiXinPayService.CloseInterface(out_trade_no);
                //result = new Result(false, "二维码超时");
                break;
            }
        }
        return result;
    }
}

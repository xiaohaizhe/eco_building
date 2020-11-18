package com.giot.eco_building.thread;

import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.utils.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-25
 * Time: 17:32
 */
public class ShapeThread implements Runnable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        String ip = null;
        /*synchronized (this) {
            if (!StringUtils.isEmpty(ProxyUtil.IP_TIME)
                    && (new Date().getTime() - ProxyUtil.IP_TIME) < 5 * 60 * 1000) {
                //ip时间没有过期
                ip = ProxyUtil.PROXY_IP;
            } else {
                boolean flag_ip = true;
                while (flag_ip) {
                    try {
                        JSONObject ipPortJson = ProxyUtil.getIpPort();
                        if (!StringUtils.isEmpty(ipPortJson.getString("ip"))) {
                            ip = ipPortJson.getString("ip");
                            ProxyUtil.IP_TIME = new Date().getTime();
                            ProxyUtil.PROXY_IP = ip;
                            flag_ip = false;//ip获取
                        } else {
                            logger.error("代理ip获取出错");
                            break;
                        }
                    } catch (IOException e) {
                        logger.error("代理ip获取出错:{}", e.getMessage());
                    }
                }
            }
        }*/
        if (!StringUtils.isEmpty(ip)){
            
        }

    }
}

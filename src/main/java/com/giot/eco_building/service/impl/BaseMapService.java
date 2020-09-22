package com.giot.eco_building.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.model.IPBean;
import com.giot.eco_building.service.MapService;
import com.giot.eco_building.utils.HttpUtil;
import com.giot.eco_building.utils.ProxyUtil;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.CookieManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-14
 * Time: 16:01
 */
@Service
public class BaseMapService implements MapService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static Map<String, String> POIID_SHAPE_MAP = new ConcurrentHashMap<>();

    @Value("${geo.key}")
    private String key;

    @Value("${geo.address.url.around}")
    private String AROUND_URL;

    @Value("${geo.address.url.search}")
    private String TEXT_URL;

    @Value("${geo.address.url.detail}")
    private String DETAIL_URL;

    private static String ip_port = "121.239.184.90:10001";
    private static boolean flag = false;


    @Override
    public String getPoiId(Project project) {
        String poiId = "";
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("location", String.format("%.6f", project.getLongitude()) + ',' + String.format("%.6f", project.getLatitude()));
        String name = project.getName();
        if (name != null && !name.equals("")) params.put("keywords", name);
        boolean flag = true;
        try {
            JSONObject result = HttpUtil.get(AROUND_URL, params);
            if (result.get("info") != null) {
                String info = (String) result.get("info");
                if (info.equals("OK")) {
                    JSONArray pois = (JSONArray) result.get("pois");
                    for (Object poi :
                            pois) {
                        JSONObject poiJson = (JSONObject) poi;
                        String poiName = poiJson.getString("name");
                        if (!StringUtils.isEmpty(poiName) &&
                                (name.equals(poiName) || poiName.contains(name) || name.contains(poiName))) {
                            poiId = poiJson.getString("id");
                            flag = false;
                        }
                    }
                }
            }
            if (flag) {
                Map<String, Object> params2 = new HashMap<>();
                params2.put("key", key);
                params2.put("keywords", name);
                params2.put("city", project.getCity());
                JSONObject result2 = HttpUtil.get(TEXT_URL, params2);
                if (result2.get("info") != null) {
                    String info = (String) result2.get("info");
                    if (info.equals("OK")) {
                        JSONArray pois = (JSONArray) result2.get("pois");
                        for (Object poi :
                                pois) {
                            JSONObject poiJson = (JSONObject) poi;
                            String poiAddress = poiJson.getString("address");
                            String address = project.getAddress();
                            if (!StringUtils.isEmpty(poiAddress) &&
                                    (address.equals(poiAddress) || poiAddress.contains(address) || address.contains(poiAddress))) {
                                poiId = poiJson.getString("id");
                                flag = false;
                            }
                        }
                    }
                }
            }
            if (flag) {
                Map<String, Object> params3 = new HashMap<>();
                params3.put("key", key);
                params3.put("keywords", name + "\\|" + project.getAddress());
                params3.put("city", project.getCity());
                JSONObject result2 = HttpUtil.get(TEXT_URL, params3);
                if (result2.get("info") != null) {
                    String info = (String) result2.get("info");
                    if (info.equals("OK")) {
                        JSONArray pois = (JSONArray) result2.get("pois");
                        for (Object poi :
                                pois) {
                            JSONObject poiJson = (JSONObject) poi;
                            String poiAddress = poiJson.getString("address");
                            String address = project.getAddress();
                            if (!StringUtils.isEmpty(poiAddress) &&
                                    (address.equals(poiAddress) || poiAddress.contains(address) || address.contains(poiAddress))) {
                                poiId = poiJson.getString("id");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return poiId;
    }

    @Override
    public String getDistrictLocation(String poiId) throws IOException {
        String shape = null;
        if (!flag) {
//            ip_port = ProxyUtil.getIpPort();
        }
        String ip = ip_port.split(":")[0];
        int port = Integer.valueOf(ip_port.split(":")[1]);
        IPBean ipBean = new IPBean();
        ipBean.setIp(ip);
        ipBean.setPort(port);
        ipBean.setType(IPBean.TYPE_HTTPS);
        ProxyUtil.setGlobalProxy(ipBean);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //设置连接超时时间
        builder.connectTimeout(1, TimeUnit.MINUTES);
        //设置代理,需要替换
        CookieManager cookieManager = new CookieManager();
        OkHttpClient client = builder
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request cookieRequest = new Request.Builder()
                .url("https://www.amap.com/detail/get/detail?id=" + poiId)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("Sec-Fetch-Site", "none")
                .addHeader("Sec-Fetch-Mode", "navigate")
                .addHeader("Sec-Fetch-Dest", "document")
                .addHeader("Host", "www.amap.com")
                .addHeader("Cookie", "cna=juWpFrjUbkICAbRvIhpEAGY/; UM_distinctid=172a22b3403cd-025783dfd1abe-f7d123e-144000-172a22b34041ab; _uab_collina=159289891108067669234884; passport_login=MjQzODUzODkxLGFtYXBCRWUyQndhRTYsdXQ0eDducXVzbndwaWx2aG96Mnlkbmpkdmt0ZXdhNmUsMTU5OTE5MDE3NSxPR0kxWldReU9UUm1aR0kzWW1JNE9XWTJabVF4TnpFek1qRXhNRFV5T0RFPQ%3D%3D; dev_help=eCbuaS1xMh7wlOzYBdrapmNjZmNjZGI4ODkyYmVmMDE4YjJhYjZhMjhmM2RjM2Q4ZDU3OTRiZWRhNWM5MmUzNTIyZDY3NWYzZGZjNjA5ZWVnghOhMt1jYcJ94ZqiRLv5AL1gAEicPjWLThcUmCes0ftOg%2FFfj%2Fqt%2FyZonEJstUp%2BF5cC8FHAwZGwXQWYobmhXBRjZ3lTDGWL0n8VYJJBq3s2EoGFnOcnbp5f4WIOE6Y%3D; guid=3b35-4bcf-1fba-7e1f; CNZZDATA1255626299=630942793-1592893582-https%253A%252F%252Fwww.baidu.com%252F%7C1600438585; xlly_s=1; l=eBEDtl3mO_6-OPDbBOfanurza77OSIRYYuPzaNbMiOCPOwCB5tv5WZru6O86C3GVh689R3uV-FgpBeYBq3xonxvTkhDeAHMmn; tfstk=cGxlBoDTeU77SYbD50sWCb1uXLnOwcgPNH-20nDdNm-U8yfcmrzZMDHcwQxLR; isg=BBERTbX0KkewuEYGQ4jSiALbIB2rfoXwYH__H_OmLVj3mjDsO8uxwKj7OG58kh0o")
                .addHeader("if-not-match","W/\"4d2-niG8NqRaZAqPiZFWMswZzBOZEeo\"")
                .get()
                .build();
        Response execute = null;
        try {
            execute = client.newCall(cookieRequest).execute();
        } catch (IOException e) {
            flag = false;
        }
        JSONObject result = JSONObject.parseObject(execute.body().string());
//        logger.info(result.toString());
        if (result.get("status") != null && result.getInteger("status") == 1) {
            if (result.get("data") != null) {
                JSONObject data = (JSONObject) result.get("data");
                if (data.get("spec") != null) {
                    JSONObject spec = (JSONObject) data.get("spec");
                    if (spec.get("mining_shape") != null) {
                        JSONObject mining_shape = (JSONObject) spec.get("mining_shape");
                        if (mining_shape.get("shape") != null) {
//                            logger.info(mining_shape.toString());
                            shape = mining_shape.getString("shape");
                            System.out.println("shape:");
//                            System.out.println(shape);
                        }
                    }
                }
            }
        }
//        execute.close();
        return shape;
    }

}


package com.giot.eco_building.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: pyt
 * @Date: 2020/6/11 15:24
 * @Description:
 */
public class HttpUtil {
    private static OkHttpClient client = new OkHttpClient();

    private static String getUrl(String url, Map<String, Object> params) {
        StringBuffer sb = new StringBuffer(url);
        sb.append('?');
        for (String key :
                params.keySet()) {
            sb.append(key).append('=').append(params.get(key)).append('&');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static JSONObject get(String url, Map<String, Object> params) throws IOException {
        Request request = new Request.Builder()
                .url(getUrl(url, params))
                .build();
        try (Response response = client.newCall(request).execute()) {
            return JSONObject.parseObject(response.body().string());
        }
    }

    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


    public static void main(String[] args) throws IOException {
        /*Map<String, Object> params = new HashMap<>();
        params.put("address", "南京市玄武区苏宁青创园");
        params.put("output", "json");
        params.put("ak", "vEe2dQodlX1GvI6L9qRIvf753savFWFt");
        JSONObject result = HttpUtil.get("http://api.map.baidu.com/geocoding/v3/", params);
        int status = result.getInteger("status");
        if (status == 0) {
            JSONObject location = result.getJSONObject("result");
            float lng = location.getFloat("lng");
            float lat = location.getFloat("lat");
        }*/
        // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以
        System.getProperties().setProperty("http.proxyHost", "45.177.16.136");
        System.getProperties().setProperty("http.proxyPort", "999");
        // 判断代理是否设置成功
        // 发送 GET 请求
        System.out.println(sendGet(
                "https://www.amap.com/detail/get/detail",
                "id=B0FFLHVJLJ"));
    }
}

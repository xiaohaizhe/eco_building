package com.giot.eco_building.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
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

    /*public static void main(String[] args) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("address", "南京市玄武区苏宁青创园");
        params.put("output", "json");
        params.put("ak", "vEe2dQodlX1GvI6L9qRIvf753savFWFt");
        JSONObject result = HttpUtil.get("http://api.map.baidu.com/geocoding/v3/", params);
        int status = result.getInteger("status");
        if (status == 0) {
            JSONObject location = result.getJSONObject("result");
            float lng = location.getFloat("lng");
            float lat = location.getFloat("lat");
        }
    }*/
}

package com.giot.eco_building.utils;

import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.model.IPBean;
import okhttp3.*;

import java.io.IOException;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.giot.eco_building.utils.HttpUtil.get;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-20
 * Time: 16:35
 */
public class ProxyUtil {
    private static final String get_ip_url = "https://too.ueuz.com/frontapi/public/http/get_ip/index";

    /**
     * 设置代理
     * @param ipBean 代理
     */
    public static void setGlobalProxy(IPBean ipBean) {
        Properties prop = System.getProperties();
        // 设置http访问要使用的代理服务器的地址
        prop.setProperty("http.proxyHost", ipBean.getIp());
        // 设置http访问要使用的代理服务器的端口
        prop.setProperty("http.proxyPort", String.valueOf(ipBean.getPort()));
        // 设置不需要通过代理服务器访问的主机，可以使用*通配符，多个地址用|分隔
//        prop.setProperty("http.nonProxyHosts", "localhost|192.168.0.*");
        // 设置安全访问使用的代理服务器地址与端口
        // 它没有https.nonProxyHosts属性，它按照http.nonProxyHosts 中设置的规则访问
        prop.setProperty("https.proxyHost", ipBean.getIp());
        prop.setProperty("https.proxyPort", String.valueOf(ipBean.getPort()));
    }

    private static String proxy_url = "https://too.ueuz.com/frontapi/public/http/get_ip/index";
    private static String auth_key = "";

    /*public static void getIp() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("auth_key", "1Y8vZ0C9L");
        params.put("show_public_ip", 1);
        params.put("show_expire", true);
        params.put("show_carrier", true);
        params.put("show_city", true);
        params.put("separator", 1);
        params.put("duplicate", 1);
        params.put("resulttype", "json");
        params.put("area", null);
        params.put("areatype", 1);
        params.put("protocol", 0);
        params.put("ipcount", 1);
        params.put("iptimelong", 1);
        params.put("type", -1);
        JSONObject res = HttpUtil.get(proxy_url, params);
        System.out.println(res);
        if (res.getInteger("state") == 0) {

        }

    }*/
    public static JSONObject getIpPort() throws IOException {
        //设置代理,需要替换
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        CookieManager cookieManager = new CookieManager();
        OkHttpClient client = builder
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Map<String, Object> params = new HashMap<>();
        params.put("type", -1);
        params.put("iptimelong", 1);
        params.put("ipcount", 1);
        params.put("protocol", 0);
        params.put("areatype", 1);
        params.put("area", null);
        params.put("resulttype", "json");
        params.put("duplicate", 0);
        params.put("separator", 1);
        params.put("other", null);
        params.put("show_city", 0);
        params.put("show_carrier", 0);
        params.put("show_expire", 0);
        params.put("isp", -1);
        params.put("auth_key", "375c51cbe311019924625fc6074706f0");
        params.put("app_key", "68827d59d8784ca7eae70224d50db79d");
        params.put("timestamp", 1600653031);
        params.put("sign", "75EF58F70354509F7B8FC7CFEE0CC00E");
        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", "PostmanRuntime/7.26.5");
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");
        headers.put("Cache-Control", "no-cache");
        /*headers.put("Host",);
        headers.put("Postman-Token",);*/


        String url = HttpUtil.getUrl(get_ip_url, params);
        Request cookieRequest = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "PostmanRuntime/7.26.5")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cache-Control", "no-cache")
//                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
//                .addHeader("Upgrade-Insecure-Requests", "1")
//                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
//                .addHeader("Sec-Fetch-user", "?1")
//                .addHeader("Sec-Fetch-Site", "none")
//                .addHeader("Sec-Fetch-Mode", "navigate")
//                .addHeader("Sec-Fetch-Dest", "document")
//                .addHeader("Cookie", "UM_distinctid=174a95dc10b516-04a9354cdee352-333769-144000-174a95dc10c270; PHPSESSID=thi8a0u6hm9198kb8cp9piegb9")
//                .addHeader("cache-control", "max-age=0")
                .addHeader("accept-language", "zh-CN,zh;q=0.9")
//                .addHeader("accept-encoding", "gzip, deflate, br")
//                .addHeader(":scheme", "https")
//                .addHeader(":path", "/frontapi/public/http/get_ip/index?type=-1&iptimelong=1&ipcount=1&protocol=0&areatype=1&area=&resulttype=txt&duplicate=0&separator=1&other=&show_city=0&show_carrier=0&show_expire=0&isp=-1&auth_key=375c51cbe311019924625fc6074706f0&app_key=68827d59d8784ca7eae70224d50db79d&timestamp=1600598992&sign=9521D75A22739FD9547B2A38DD969084")
//                .addHeader(":method", "GET")
//                .addHeader(":authority", "too.ueuz.com")
                .get()
                .build();
        Response execute = client.newCall(cookieRequest).execute();
        System.out.println(execute.body().string());
//        return JSONObject.parseObject(execute.body().string());
        return null;
    }

        public static void main(String[] args) throws IOException {
        String ip = "115.203.123.200";
        int port = 10001;
        String poiId = "B01FE0ICEW";
        IPBean ipBean = new IPBean();
        ipBean.setIp(ip);
        ipBean.setPort(port);
        ipBean.setType(IPBean.TYPE_HTTPS);
        setGlobalProxy(ipBean);
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
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
                .addHeader("Cookie", "cna=juWpFrjUbkICAbRvIhpEAGY/; UM_distinctid=172a22b3403cd-025783dfd1abe-f7d123e-144000-172a22b34041ab; _uab_collina=159289891108067669234884; passport_login=MjQzODUzODkxLGFtYXBCRWUyQndhRTYsdXQ0eDducXVzbndwaWx2aG96Mnlkbmpkdmt0ZXdhNmUsMTU5OTE5MDE3NSxPR0kxWldReU9UUm1aR0kzWW1JNE9XWTJabVF4TnpFek1qRXhNRFV5T0RFPQ%3D%3D; dev_help=eCbuaS1xMh7wlOzYBdrapmNjZmNjZGI4ODkyYmVmMDE4YjJhYjZhMjhmM2RjM2Q4ZDU3OTRiZWRhNWM5MmUzNTIyZDY3NWYzZGZjNjA5ZWVnghOhMt1jYcJ94ZqiRLv5AL1gAEicPjWLThcUmCes0ftOg%2FFfj%2Fqt%2FyZonEJstUp%2BF5cC8FHAwZGwXQWYobmhXBRjZ3lTDGWL0n8VYJJBq3s2EoGFnOcnbp5f4WIOE6Y%3D; guid=3b35-4bcf-1fba-7e1f; CNZZDATA1255626299=630942793-1592893582-https%253A%252F%252Fwww.baidu.com%252F%7C1600438585; xlly_s=1; gray_auth=2; l=eBEDtl3mO_6-OPDbBOfanurza77OSIRYYuPzaNbMiOCPOwCB5tv5WZru6O86C3GVh689R3uV-FgpBeYBq3xonxvTkhDeAHMmn; tfstk=cGxlBoDTeU77SYbD50sWCb1uXLnOwcgPNH-20nDdNm-U8yfcmrzZMDHcwQxLR; isg=BBERTbX0KkewuEYGQ4jSiALbIB2rfoXwYH__H_OmLVj3mjDsO8uxwKj7OG58kh0o")
                .get()
                .build();
        Response execute = client.newCall(cookieRequest).execute();
        JSONObject result = JSONObject.parseObject(execute.body().string());
            System.out.println(result.toString());
        if (result.get("status") != null && result.getInteger("status") == 1) {
            if (result.get("data") != null) {
                JSONObject data = (JSONObject) result.get("data");
                if (data.get("spec") != null) {
                    JSONObject spec = (JSONObject) data.get("spec");
                    if (spec.get("mining_shape") != null) {
                        JSONObject mining_shape = (JSONObject) spec.get("mining_shape");
                        if (mining_shape.get("shape") != null) {
//                            logger.info(mining_shape.toString());
                            String shape = mining_shape.getString("shape");
                            System.out.println("shape:");
                            System.out.println(shape);
                        }
                    }
                }
            }
        }
        execute.close();

    }
    /*public static void main(String[] args) throws IOException {
        System.out.println(getIpPort());
    }*/
}

package com.giot.eco_building.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.model.IPBean;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-20
 * Time: 16:35
 */
public class ProxyUtil {
    //缓存ip变量
    public static Map<String, JSONObject> cacheVariable = new ConcurrentHashMap<>();
    //设置ip的过期时间为5min
    private final static int TIME_OUT = 5 * 60 * 1000;

    /**
     * 设置代理
     *
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

    public static JSONObject getIpForProxy() {
        //检查是否过期 我这里设置了5min过期
        JSONObject ipJson = cacheVariable.get("ip");
        if (ipJson != null && (System.currentTimeMillis() - ipJson.getLong("get_time")) < TIME_OUT) {
            return ipJson;
        }
        JSONObject obj = null;
        try {
            obj = getIpPort();
            obj.put("get_time", System.currentTimeMillis());//此处设置获取时间，用于比对过期时间
        } catch (IOException e) {
            e.printStackTrace();
        }
        cacheVariable.put("ip", obj);
        return obj;
    }

    public static JSONObject getIpPort() throws IOException {
        //设置代理,需要替换
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        CookieManager cookieManager = new CookieManager();
        OkHttpClient client = builder
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        String url = "https://too.ueuz.com/frontapi/public/http/get_ip/index?type=-1&iptimelong=1&ipcount=1&protocol=0&areatype=1&area=&resulttype=json&duplicate=0&separator=1&other=&show_city=0&show_carrier=0&show_expire=0&isp=-1&auth_key=375c51cbe311019924625fc6074706f0&app_key=68827d59d8784ca7eae70224d50db79d&timestamp=1601025920&sign=DDE4848C591BF480017EF647AC63CC7E";
        Request cookieRequest = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response execute = client.newCall(cookieRequest).execute();
        String result = execute.body().string();
        JSONArray resultArray = (JSONArray) JSONArray.parse(result);
        return (JSONObject) resultArray.get(0);
    }

    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException, IOException, KeyManagementException {
        /*while (true) {
            JSONObject ipJson = getIpForProxy();
            System.out.println(ipJson);
            String domain = ipJson.getString("domain");
            Integer ip_port = ipJson.getInteger("ip_port");
            IPBean ipBean = new IPBean();
            ipBean.setIp(domain);
            ipBean.setPort(ip_port);
            setGlobalProxy(ipBean);
            JSONObject jsonObject = HttpUtil.getResponseContent("https://www.amap.com/detail/get/detail?id=B0FFLHVJLJ",null,ipBean);
            System.out.println(jsonObject);
            String result = HttpUtil.sendGet("https://www.amap.com/detail/get/detail?id=B0FFLHVJLJ", null);
            System.out.println(result);
            long sleepTime = 60000 + (long) (Math.random() * 30000);
            System.out.println("休息："+sleepTime/1000+"s");
            Thread.sleep(sleepTime);
        }*/
        String result = HttpUtil.sendGet("https://www.amap.com/detail/get/detail?id=B0FFLHVJLJ", null);
        System.out.println(result);
    }
}

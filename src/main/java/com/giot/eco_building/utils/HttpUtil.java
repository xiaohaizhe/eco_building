package com.giot.eco_building.utils;

import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.model.IPBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: pyt
 * @Date: 2020/6/11 15:24
 * @Description:
 */
public class HttpUtil {
    private static OkHttpClient client = new OkHttpClient();
    private static Map<Long, String> ipMap = new HashMap<>();

    /**
     * @param url
     * @param headerMap 请求头部
     * @param ipBean
     * @return
     * @throws Exception
     */
    public static JSONObject getResponseContent(String url, Map<String, List<String>> headerMap, IPBean ipBean) throws Exception {
        HttpsURLConnection connection = null;

        // 设置代理
        if (ipBean != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipBean.getIp(), ipBean.getPort()));

            connection = (HttpsURLConnection) new URL(url).openConnection(proxy);

            if (ipBean.getType() == IPBean.TYPE_HTTPS) {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
                connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
            }
        }

        if (connection == null)
            connection = (HttpsURLConnection) new URL(url).openConnection();

        // 添加请求头部
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36");
        if (headerMap != null) {
            Iterator<Map.Entry<String, List<String>>> iterator = headerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<String>> entry = iterator.next();
                List<String> values = entry.getValue();
                for (String value : values)
                    connection.setRequestProperty(entry.getKey(), value);
            }
        }

        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        inputStream.close();
        return JSONObject.parseObject(stringBuilder.toString());
    }


    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static String getUrl(String url, Map<String, Object> params) {
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
//            System.out.println("发送GET请求出现异常！" + e);
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
        /*System.getProperties().setProperty("http.proxyHost", "45.177.16.136");
        System.getProperties().setProperty("http.proxyPort", "999");
        // 判断代理是否设置成功
        // 发送 GET 请求
        System.out.println(sendGet(
                "https://www.amap.com/detail/get/detail",
                "id=B0FFLHVJLJ"));*/
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        while (true) {
            System.out.println(sdf.format(new Date()));
            long sleepTime = 60000 + (long) (Math.random() * 30000);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        while (true){
            String ip_port = null;
            if (ipMap.isEmpty()) {
                String res = HttpUtil.sendGet("https://too.ueuz.com/frontapi/public/http/get_ip/index?type=21232&iptimelong=1&ipcount=1&protocol=0&areatype=1&area=&resulttype=txt&duplicate=0&separator=1&other=&show_city=0&show_carrier=0&show_expire=0&isp=-1&auth_key=fc0cfcd5a62d26d4dee8888751fee5c4&app_key=68827d59d8784ca7eae70224d50db79d&timestamp=1600595192&sign=D3B4D965759270F23DB47EC863AA1AAC", "");
                ip_port = res;
                ipMap.put(new Date().getTime(), res);
            } else {
                for (Long key :
                        ipMap.keySet()) {
                    Date now = new Date();
                    if ((now.getTime() - key) > 5 * 60 * 1000) {
                        String res = HttpUtil.sendGet("https://too.ueuz.com/frontapi/public/http/get_ip/index?type=21232&iptimelong=1&ipcount=1&protocol=0&areatype=1&area=&resulttype=txt&duplicate=0&separator=1&other=&show_city=0&show_carrier=0&show_expire=0&isp=-1&auth_key=fc0cfcd5a62d26d4dee8888751fee5c4&app_key=68827d59d8784ca7eae70224d50db79d&timestamp=1600595192&sign=D3B4D965759270F23DB47EC863AA1AAC", "");
                        ip_port = res;
                        ipMap.remove(key);
                        ipMap.put(new Date().getTime(), res);
                    } else {
                        ip_port = ipMap.get(key);
                    }
                }
            }
            String[] ipport = ip_port.split(":");
            try {
                IPBean ipBean = new IPBean();
                ipBean.setIp(ipport[0]);
                ipBean.setPort(Integer.valueOf(ipport[1]));
                ipBean.setType(IPBean.TYPE_HTTPS);
                ProxyUtil.setGlobalProxy(ipBean);
                JSONObject res = getResponseContent("https://www.amap.com/detail/get/detail?id=B0FFLHVJLJ", null, ipBean);
                System.out.println(res);
                Thread.sleep(2*60*1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}

package cn.wildfirechat.sdk.utilities;

import cn.wildfirechat.sdk.model.IMResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ikidou.reflect.TypeBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;


/**
 * 频道HTTP工具类
 * <p>
 * 提供频道API调用的HTTP工具方法，包括初始化、请求签名、HTTP请求等。
 * 继承自HttpUtils并实现Closeable接口，专门用于频道接口的调用。
 * </p>
 */
public class ChannelHttpUtils extends HttpUtils implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelHttpUtils.class);

    private String imurl;
    private String channelId;
    private String channelSecret;
    private final CloseableHttpClient httpClient;

    private final int port;

    private void checkPort() {
        if (port != 80 && port != 443 && port != -1) {
            if (port == 18080) {
                LOG.warn("您传入的频道API地址中的端口是18080，18080端口默认为管理API端口，频道API端口应该为IM服务的HTTP端口，默认为80，请确认是否使用错误！");
            } else {
                LOG.warn("您传入的频道API地址中的端口不是80/443，频道API的端口和客户端使用端口一样，都应该为IM服务的HTTP端口，默认为80，请确实是否正确？如果您定制化了IM服务端口或者使用其他端口反向代理IM服务的HTTP端口请忽略此提示。");
            }
        }
    }

    public ChannelHttpUtils(String imurl, String channelId, String secret) {
        this.imurl = imurl.trim();
        this.channelId = channelId.trim();
        this.channelSecret = secret.trim();
        try {
            URL u = new URL(this.imurl);
            int port = u.getPort();
            if(port == -1) {
                port = u.getDefaultPort();
            }
            this.port = port;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        checkPort();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setValidateAfterInactivity(1000);
        int connectTimeout = 5000; // 连接超时时间
        int socketTimeout = 15000; // 请求超时时间
        int connectionRequestTimeout = 3000; // 从连接池获取连接的超时时间
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(connectTimeout)
            .setSocketTimeout(socketTimeout)
            .setConnectionRequestTimeout(connectionRequestTimeout)
            .build();
        httpClient = HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(cm)
            .evictExpiredConnections()
            .evictIdleConnections(60L, TimeUnit.SECONDS)
            .setRetryHandler(DefaultHttpRequestRetryHandler.INSTANCE)
            .setMaxConnTotal(100)
            .setMaxConnPerRoute(50)
            .build();
    }

    public <T> IMResult<T> httpJsonPost(String path, Object object, Class<T> clazz) throws Exception{
        return httpJsonPost(imurl, channelId, httpClient, path, object, clazz, post -> {
            int nonce = (int)(Math.random() * 100000 + 3);
            long timestamp = System.currentTimeMillis();
            String str = nonce + "|" + channelSecret + "|" + timestamp;
            String sign = DigestUtils.sha1Hex(str);

            post.setHeader("Content-type", "application/json; charset=utf-8");
            post.setHeader("Connection", "Keep-Alive");
            post.setHeader("nonce", nonce + "");
            post.setHeader("timestamp", "" + timestamp);
            post.setHeader("cid", channelId);
            post.setHeader("sign", sign);
        });
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelSecret() {
        return channelSecret;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}

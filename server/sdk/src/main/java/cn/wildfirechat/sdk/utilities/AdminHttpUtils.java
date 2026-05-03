package cn.wildfirechat.sdk.utilities;

import cn.wildfirechat.sdk.model.IMResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * 管理员HTTP工具类
 * <p>
 * 提供管理员API调用的HTTP工具方法，包括初始化、请求签名、HTTP请求等。
 * 继承自HttpUtils，专门用于管理员接口的调用。
 * </p>
 */
public class AdminHttpUtils extends HttpUtils {
    // ======================== 常量定义（消除硬编码）========================
    private static final Logger LOG = LoggerFactory.getLogger(AdminHttpUtils.class);

    // 连接池配置常量
    private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 3000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 15000;
    private static final int MAX_CONN_TOTAL = 100;
    private static final int MAX_CONN_PER_ROUTE = 50;
    private static final long IDLE_CONNECTION_EVICT_TIME = 60L;
    private static final int VALIDATE_AFTER_INACTIVITY = 1000;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int NONCE_MAX_RANGE = 1000000;

    // ======================== 线程安全的全局变量 ========================
    private static volatile String adminUrl;
    private static volatile String adminSecret;

    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    private static volatile CloseableHttpClient httpClient;


    // ======================== 初始化方法 ========================
    public static void init(String url, String secret) {
        init(url, secret, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * 初始化HTTP客户端和配置
     * @param url IM服务管理地址
     * @param secret 管理密钥
     * @param timeout 套接字超时时间（毫秒）
     */
    public static synchronized void init(String url, String secret, int timeout) {
        // 参数校验
        if (isNullOrEmpty(url) || isNullOrEmpty(secret)) {
            throw new IllegalArgumentException("IM服务地址或密钥不能为空");
        }
        adminUrl = url.trim();
        adminSecret = secret.trim();

        // 若已存在HttpClient，先关闭再重新初始化（支持配置更新）
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                LOG.error("关闭旧HttpClient连接池失败", e);
            }
        }

        // 初始化连接池
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setValidateAfterInactivity(VALIDATE_AFTER_INACTIVITY);

        // 构建请求配置
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
            .setSocketTimeout(timeout)
            .setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT)
            .build();

        // 构建HttpClient
        httpClient = HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(cm)
            .evictExpiredConnections()
            .evictIdleConnections(IDLE_CONNECTION_EVICT_TIME, TimeUnit.SECONDS)
            .setRetryHandler(new DefaultHttpRequestRetryHandler(3, false)) // 重试3次，不重试IO异常
            .setMaxConnTotal(MAX_CONN_TOTAL)
            .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
            .build();

        LOG.info("AdminHttpUtils初始化完成，IM服务地址：{}", adminUrl);
    }

    // ======================== HTTP GET请求 ========================
    public static <T> IMResult<T> httpGet(String path, Class<T> clazz) throws Exception {
        // 前置校验
        validateInitStatus(adminUrl, adminSecret, httpClient);
        if (isNullOrEmpty(path)) {
            throw new IllegalArgumentException("请求路径不能为空");
        }

        HttpGet httpGet = new HttpGet(adminUrl + path);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            return handleResponse(response, clazz, adminUrl, adminSecret);
        } catch (Exception e) {
            LOG.error("HTTP GET请求失败，路径：{}", path, e);
            throw new Exception("HTTP GET请求异常：" + e.getMessage(), e);
        } finally {
            httpGet.releaseConnection();
        }
    }

    // ======================== HTTP JSON POST请求 ========================
    public static <T> IMResult<T> httpJsonPost(String path, Object object, Class<T> clazz) throws Exception {
        return httpJsonPost(adminUrl, adminSecret, httpClient, path, object, clazz, httpPost -> {
            // 构建签名头
            int nonce = SECURE_RANDOM.nextInt(NONCE_MAX_RANGE) + 1; // 安全随机数
            long timestamp = System.currentTimeMillis();
            String signStr = nonce + "|" + adminSecret + "|" + timestamp;
            String sign = DigestUtils.sha1Hex(signStr);

            // 设置请求头
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Connection", "Keep-Alive");
            httpPost.setHeader("nonce", String.valueOf(nonce));
            httpPost.setHeader("timestamp", String.valueOf(timestamp));
            httpPost.setHeader("sign", sign);
        });
    }
}

package cn.wildfirechat.sdk.utilities;

import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.sdk.model.IMResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * HTTP工具类
 * <p>
 * 提供HTTP请求相关的工具方法，包括：
 * <ul>
 * <li>HTTP POST请求</li>
 * <li>HTTP GET请求</li>
 * <li>响应处理</li>
 * <li>日志记录</li>
 * </ul>
 * </p>
 */
public class HttpUtils extends JsonUtils {
    // ======================== 常量定义（消除硬编码）========================
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static interface HeaderCallback {
        public void addHeaders(HttpPost httpPost);
    }

    // ======================== HTTP JSON POST请求 ========================
    public static <T> IMResult<T> httpJsonPost(String adminUrl, String adminSecret, HttpClient httpClient, String path, Object object, Class<T> clazz, HeaderCallback headerCallback) throws Exception {
        // 前置校验
        validateInitStatus(adminUrl, adminSecret, httpClient);
        if (isNullOrEmpty(path)) {
            throw new IllegalArgumentException("请求路径不能为空");
        }

        String url = adminUrl + path;
        HttpPost httpPost = new HttpPost(url);
        try {
            headerCallback.addHeaders(httpPost);

            // 设置请求体
            String jsonStr = object == null ? "" : GSON.toJson(object);
            LOG.info("HTTP POST请求：{}，请求体：{}", url, truncateLogContent(jsonStr));
            StringEntity entity = new StringEntity(jsonStr, StandardCharsets.UTF_8);
            entity.setContentEncoding(StandardCharsets.UTF_8.name());
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            // 执行请求并处理响应
            HttpResponse response = httpClient.execute(httpPost);
            return handleResponse(response, clazz, adminUrl, adminSecret);
        } catch (Exception e) {
            LOG.error("HTTP POST请求失败，路径：{}", path, e);
            throw new Exception("HTTP POST请求异常：" + e.getMessage(), e);
        } finally {
            httpPost.releaseConnection();
        }
    }

    // ======================== 私有工具方法 ========================
    /**
     * 校验初始化状态
     */
    protected static void validateInitStatus(String adminUrl, String adminSecret, HttpClient httpClient) {
        if (isNullOrEmpty(adminUrl) || isNullOrEmpty(adminSecret) || httpClient == null) {
            String errorMsg = "野火IM Server SDK未初始化，请调用AdminConfig.initAdmin(AdminUrl, AdminSecret)完成初始化";
            LOG.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
    }

    /**
     * 处理HTTP响应，统一解析逻辑
     */
    protected static <T> IMResult<T> handleResponse(HttpResponse response, Class<T> clazz, String adminUrl, String adminSecret) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        String content = readResponseContent(response);

        // 非200状态码处理
        if (statusCode != HttpStatus.SC_OK) {
            String errorMsg = String.format("HTTP请求失败，状态码：%d，响应内容：%s", statusCode, truncateLogContent(content));
            LOG.error(errorMsg);
            throw new Exception(errorMsg);
        }

        // 解析响应体
        IMResult<T> result = fromJsonObject(content, clazz);
        if (result != null) {
            if (result.getErrorCode() == ErrorCode.ERROR_CODE_AUTH_FAILURE) {
                LOG.error("鉴权失败，请检查IM服务地址({})或密钥({})配置", adminUrl, maskSecret(adminSecret));
            } else if (result.getErrorCode() == ErrorCode.ERROR_CODE_SIGN_EXPIRED) {
                LOG.error("签名过期，请确保当前服务与IM服务({})时间同步", adminUrl);
            }
        }
        return result;
    }

    /**
     * 读取响应体（兼容分块传输，修复getContentLength=-1问题）
     */
    protected static String readResponseContent(HttpResponse response) throws IOException {
        if (response.getEntity() == null) {
            return "";
        }

        // 使用try-with-resources自动关闭所有流，避免资源泄漏
        try (InputStream is = response.getEntity().getContent();
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            String nl = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) {
                sb.append(line).append(nl);
            }
            String content = sb.toString().trim();
            LOG.info("HTTP响应内容：{}", truncateLogContent(content));
            return content;
        }
    }

    /**
     * 截断超长日志内容（避免日志爆炸）
     */
    protected static String truncateLogContent(String content) {
        if (content == null) {
            return "";
        }
        int maxLength = 1024;
        return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
    }

    /**
     * 掩码处理密钥（避免日志泄露敏感信息）
     */
    protected static String maskSecret(String secret) {
        if (isNullOrEmpty(secret) || secret.length() <= 4) {
            return "******";
        }
        return secret.substring(0, 2) + "******" + secret.substring(secret.length() - 2);
    }

    /**
     * 空值判断（补充实现，避免依赖外部未定义方法）
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

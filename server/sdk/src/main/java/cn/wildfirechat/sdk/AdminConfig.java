package cn.wildfirechat.sdk;

import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

/**
 * 管理员配置类
 * <p>
 * 用于初始化管理员SDK的配置，包括IM服务器URL和密钥。
 * 在使用任何Admin功能之前，必须先调用initAdmin方法进行初始化。
 * </p>
 */
public class AdminConfig {
    /**
     * 初始化管理员配置
     * <p>
     * 在使用任何Admin功能之前，必须先调用此方法进行初始化。
     * </p>
     * @param url IM服务器地址，例如: http://your-im-server.com
     * @param secret 管理员密钥，在服务器配置中设置
     */
    public static void initAdmin(String url, String secret) {
        AdminHttpUtils.init(url, secret);
    }
}

package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

import java.util.List;

/**
 * 敏感词管理类
 * <p>
 * 提供敏感词管理相关的功能，包括：
 * <ul>
 * <li>添加敏感词</li>
 * <li>删除敏感词</li>
 * <li>查询敏感词列表</li>
 * </ul>
 * </p>
 */
public class SensitiveAdmin {
    /**
     * 添加敏感词
     * @param sensitives 敏感词列表
     * @return 添加结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> addSensitives(List<String> sensitives) throws Exception {
        String path = APIPath.Sensitive_Add;
        InputOutputSensitiveWords input = new InputOutputSensitiveWords();
        input.setWords(sensitives);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 删除敏感词
     * @param sensitives 敏感词列表
     * @return 删除结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> removeSensitives(List<String> sensitives) throws Exception {
        String path = APIPath.Sensitive_Del;
        InputOutputSensitiveWords input = new InputOutputSensitiveWords();
        input.setWords(sensitives);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取敏感词列表
     * @return 敏感词列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<InputOutputSensitiveWords> getSensitives() throws Exception {
        String path = APIPath.Sensitive_Query;
        return AdminHttpUtils.httpJsonPost(path, null, InputOutputSensitiveWords.class);
    }

}

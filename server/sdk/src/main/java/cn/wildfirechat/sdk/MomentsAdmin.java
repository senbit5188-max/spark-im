package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.pojos.moments.FeedPojo;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

/**
 * 朋友圈管理类
 * <p>
 * 提供朋友圈（动态）管理相关的功能。
 * </p>
 */
public class MomentsAdmin {
    /**
     * 发布朋友圈动态
     * @param feedPojo 动态内容
     * @return 发布结果，包含消息ID
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<SendMessageResult> postFeeds(FeedPojo feedPojo) throws Exception {
        String path = APIPath.Admin_Moments_Post_Feed;
        return AdminHttpUtils.httpJsonPost(path, feedPojo, SendMessageResult.class);
    }
}

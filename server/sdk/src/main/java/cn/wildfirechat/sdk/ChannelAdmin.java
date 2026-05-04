package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

/**
 * 频道管理类
 * <p>
 * 提供频道管理相关的功能，包括：
 * <ul>
 * <li>创建和销毁频道</li>
 * <li>获取频道信息</li>
 * <li>用户订阅/取消订阅频道</li>
 * <li>检查用户是否订阅频道</li>
 * </ul>
 * </p>
 */
public class ChannelAdmin {
    /**
     * 创建频道
     * @param inputCreateChannel 频道创建信息
     * @return 创建结果，包含频道ID
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputCreateChannel> createChannel(InputCreateChannel inputCreateChannel) throws Exception {
        String path = APIPath.Create_Channel;
        return AdminHttpUtils.httpJsonPost(path, inputCreateChannel, OutputCreateChannel.class);
    }

    /**
     * 销毁频道
     * @param channelId 频道ID
     * @return 销毁结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> destroyChannel(String channelId) throws Exception {
        String path = APIPath.Destroy_Channel;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, Void.class);
    }

    /**
     * 获取频道信息
     * @param channelId 频道ID
     * @return 频道信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputGetChannelInfo> getChannelInfo(String channelId) throws Exception {
        String path = APIPath.Get_Channel_Info;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, OutputGetChannelInfo.class);
    }

    /**
     * 订阅频道
     * @param channelId 频道ID
     * @param userId 用户ID
     * @return 订阅结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> subscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 1);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 取消订阅频道
     * @param channelId 频道ID
     * @param userId 用户ID
     * @return 取消订阅结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> unsubscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 检查用户是否订阅了频道
     * @param userId 用户ID
     * @param channelId 频道ID
     * @return true-已订阅，false-未订阅
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputBooleanValue> isUserSubscribedChannel(String userId, String channelId) throws Exception {
        String path = APIPath.Check_User_Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, OutputBooleanValue.class);
    }
}

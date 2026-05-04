package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;
import cn.wildfirechat.sdk.utilities.ChannelHttpUtils;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import static cn.wildfirechat.proto.ProtoConstants.ApplicationType.ApplicationType_Channel;

/**
 * 频道服务类（仅专业版支持，社区版不支持）
 * <p>
 * 提供频道相关的功能，包括：
 * <ul>
 * <li>获取用户信息</li>
 * <li>修改频道信息</li>
 * <li>发送和撤回消息</li>
 * <li>用户订阅管理</li>
 * <li>订阅者列表查询</li>
 * </ul>
 * </p>
 */
public class ChannelServiceApi implements Closeable {
    private final ChannelHttpUtils channelHttpUtils;

    /**
     * 创建频道服务实例
     * @param imurl IM服务器地址
     * @param channelId 频道ID
     * @param secret 频道密钥
     */
    public ChannelServiceApi(String imurl, String channelId, String secret) {
        channelHttpUtils = new ChannelHttpUtils(imurl, channelId, secret);
    }

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<InputOutputUserInfo> getUserInfo(String userId) throws Exception {
        String path = APIPath.Channel_User_Info;
        InputGetUserInfo getUserInfo = new InputGetUserInfo(userId, null, null);
        return channelHttpUtils.httpJsonPost(path, getUserInfo, InputOutputUserInfo.class);
    }

    /**
     * 根据用户名获取用户信息
     * @param userName 用户名
     * @return 用户信息
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<InputOutputUserInfo> getUserInfoByName(String userName) throws Exception {
        String path = APIPath.Channel_User_Info;
        InputGetUserInfo getUserInfo = new InputGetUserInfo(null, userName, null);
        return channelHttpUtils.httpJsonPost(path, getUserInfo, InputOutputUserInfo.class);
    }

    /**
     * 根据手机号获取用户信息
     * @param mobile 手机号
     * @return 用户信息
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<InputOutputUserInfo> getUserInfoByMobile(String mobile) throws Exception {
        String path = APIPath.Channel_User_Info;
        InputGetUserInfo getUserInfo = new InputGetUserInfo(null, null, mobile);
        return channelHttpUtils.httpJsonPost(path, getUserInfo, InputOutputUserInfo.class);
    }

    /**
     * 修改频道信息
     * @param type 修改类型，参考{@link cn.wildfirechat.proto.ProtoConstants.ModifyChannelInfoType}
     * @param value 修改的值
     * @return 操作结果
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<Void> modifyChannelInfo(/*ProtoConstants.ModifyChannelInfoType*/int type, String value) throws Exception {
        String path = APIPath.Channel_Update_Profile;
        InputModifyChannelInfo modifyChannelInfo = new InputModifyChannelInfo();
        modifyChannelInfo.setType(type);
        modifyChannelInfo.setValue(value);
        return channelHttpUtils.httpJsonPost(path, modifyChannelInfo, Void.class);
    }

    /**
     * 获取频道信息
     * @return 频道信息
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<OutputGetChannelInfo> getChannelInfo() throws Exception {
        String path = APIPath.Channel_Get_Profile;
        return channelHttpUtils.httpJsonPost(path, null, OutputGetChannelInfo.class);
    }

    /**
     * 修改频道菜单
     * @param menus 菜单列表
     * @return 操作结果
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<Void> modifyChannelMenu(List<PojoChannelMenu> menus) throws Exception {
        String menuStr = new Gson().toJson(menus);
        return modifyChannelInfo(ProtoConstants.ModifyChannelInfoType.Modify_Channel_Menu, menuStr);
    }

    /**
     * 发送频道消息
     * @param line 线路
     * @param targets 目标用户ID列表
     * @param payload 消息内容
     * @return 发送结果
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<SendMessageResult> sendMessage(int line, List<String> targets, MessagePayload payload) throws Exception {
        String path = APIPath.Channel_Message_Send;
        SendChannelMessageData messageData = new SendChannelMessageData();
        messageData.setLine(line);
        messageData.setTargets(targets);
        messageData.setPayload(payload);
        return channelHttpUtils.httpJsonPost(path, messageData, SendMessageResult.class);
    }

    /**
     * 撤回频道消息
     * @param messageUid 消息UID
     * @return 操作结果，返回被撤回消息的UID
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<String> recallMessage(long messageUid) throws Exception {
        String path = APIPath.Channel_Msg_Recall;
        RecallMessageData messageData = new RecallMessageData();
        messageData.setMessageUid(messageUid);
        return channelHttpUtils.httpJsonPost(path, messageData, String.class);
    }

    /**
     * 重新发布频道消息
     * @param messageUid 消息UID
     * @param targets 目标用户ID列表
     * @return 操作结果
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<Void> republishMessage(long messageUid, List<String> targets) throws Exception {
        String path = APIPath.Channel_Msg_Republish;
        RepublishChannelMessageData messageData = new RepublishChannelMessageData();
        messageData.setMessageId(messageUid);
        messageData.setTargets(targets);
        return channelHttpUtils.httpJsonPost(path, messageData, Void.class);
    }

    /**
     * 订阅频道
     * @param userId 用户ID
     * @return 操作结果
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<Void> subscribe(String userId) throws Exception {
        String path = APIPath.Channel_Subscribe;
        InputChannelSubscribe input = new InputChannelSubscribe();
        input.setTarget(userId);
        input.setSubscribe(1);
        return channelHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 取消订阅频道
     * @param userId 用户ID
     * @return 操作结果
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<Void> unsubscribe(String userId) throws Exception {
        String path = APIPath.Channel_Subscribe;
        InputChannelSubscribe input = new InputChannelSubscribe();
        input.setTarget(userId);
        input.setSubscribe(0);
        return channelHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取频道订阅者列表
     * @return 订阅者用户ID列表
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<OutputStringList> getSubscriberList() throws Exception {
        String path = APIPath.Channel_Subscriber_List;
        return channelHttpUtils.httpJsonPost(path, null, OutputStringList.class);
    }

    /**
     * 判断用户是否订阅了频道
     * @param userId 用户ID
     * @return 是否订阅
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<Boolean> isSubscriber(String userId) throws Exception {
        String path = APIPath.Channel_Is_Subscriber;
        InputUserId input = new InputUserId(userId);
        return channelHttpUtils.httpJsonPost(path, input, Boolean.class);
    }

    /**
     * 通过授权码获取应用用户信息
     * @param authCode 授权码
     * @return 应用用户信息
     * @throws Exception 请求失败时抛出异常
     */
    public IMResult<OutputApplicationUserInfo> applicationGetUserInfo(String authCode) throws Exception {
        String path = APIPath.Channel_Application_Get_UserInfo;
        InputApplicationGetUserInfo input = new InputApplicationGetUserInfo();
        input.setAuthCode(authCode);
        return channelHttpUtils.httpJsonPost(path, input, OutputApplicationUserInfo.class);
    }

    /**
     * 获取应用签名配置
     * <p>用于客户端接入时的签名验证</p>
     * @return 应用签名配置数据
     */
    public OutputApplicationConfigData getApplicationSignature() {
        int nonce = (int)(Math.random() * 100000 + 3);
        long timestamp = System.currentTimeMillis()/1000;
        String str = nonce + "|" + channelHttpUtils.getChannelId() + "|" + timestamp + "|" + channelHttpUtils.getChannelSecret();
        String sign = DigestUtils.sha1Hex(str);
        OutputApplicationConfigData configData = new OutputApplicationConfigData();
        configData.setAppId(channelHttpUtils.getChannelId());
        configData.setAppType(ApplicationType_Channel);
        configData.setTimestamp(timestamp);
        configData.setNonceStr(nonce+"");
        configData.setSignature(sign);
        return configData;
    }

    /**
     * 关闭服务，释放资源
     * @throws IOException 关闭时发生IO异常
     */
    @Override
    public void close() throws IOException {
        channelHttpUtils.close();
    }
}

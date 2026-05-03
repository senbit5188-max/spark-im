package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

/**
 * 聊天室管理类
 * <p>
 * 提供聊天室管理相关的功能，包括：
 * <ul>
 * <li>创建和销毁聊天室</li>
 * <li>获取聊天室信息</li>
 * <li>聊天室成员管理</li>
 * <li>聊天室黑名单管理（仅专业版）</li>
 * <li>聊天室管理员设置</li>
 * <li>聊天室全员禁言</li>
 * </ul>
 * </p>
 */
public class ChatroomAdmin {
    /**
     * 创建聊天室
     * @param chatroomId 聊天室ID（为空时自动生成）
     * @param title 聊天室标题
     * @param desc 聊天室描述
     * @param portrait 聊天室头像
     * @param extra 额外信息
     * @param state 聊天室状态
     * @return 创建结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputCreateChatroom> createChatroom(String chatroomId, String title, String desc ,String portrait, String extra, Integer state) throws Exception {
        String path = APIPath.Create_Chatroom;
        InputCreateChatroom input = new InputCreateChatroom();
        input.setChatroomId(chatroomId);
        input.setTitle(title);
        input.setDesc(desc);
        input.setPortrait(portrait);
        input.setExtra(extra);
        input.setState(state);
        return AdminHttpUtils.httpJsonPost(path, input, OutputCreateChatroom.class);
    }

    /**
     * 销毁聊天室
     * @param chatroomId 聊天室ID
     * @return 销毁结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> destroyChatroom(String chatroomId) throws Exception {
        String path = APIPath.Chatroom_Destroy;
        InputDestoryChatroom input = new InputDestoryChatroom();
        input.setChatroomId(chatroomId);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取聊天室信息
     * @param chatroomId 聊天室ID
     * @return 聊天室信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputGetChatroomInfo> getChatroomInfo(String chatroomId) throws Exception {
        String path = APIPath.Chatroom_Info;
        InputGetChatroomInfo input = new InputGetChatroomInfo(chatroomId);
        return AdminHttpUtils.httpJsonPost(path, input, OutputGetChatroomInfo.class);
    }

    /**
     * 获取聊天室成员列表
     * @param chatroomId 聊天室ID
     * @return 成员ID列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputStringList> getChatroomMembers(String chatroomId) throws Exception {
        String path = APIPath.Chatroom_GetMembers;
        InputGetChatroomInfo input = new InputGetChatroomInfo(chatroomId);
        return AdminHttpUtils.httpJsonPost(path, input, OutputStringList.class);
    }

    /**
     * 获取用户所在的聊天室
     * @param userId 用户ID
     * @return 用户所在的聊天室信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputUserChatroom> getUserChatroom(String userId) throws Exception {
        String path = APIPath.Chatroom_GetUserChatroom;
        InputUserId input = new InputUserId(userId);
        return AdminHttpUtils.httpJsonPost(path, input, OutputUserChatroom.class);
    }

    /**
     * 设置聊天室黑名单状态（仅专业版支持）
     * <p>status：0-正常；1-禁言；2-禁止加入</p>
     * @param chatroomId 聊天室ID
     * @param userId 用户ID
     * @param status 状态值
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setChatroomBlacklist(String chatroomId, String userId, int status) throws Exception {
        String path = APIPath.Chatroom_SetBlacklist;
        InputSetChatroomBlacklist input = new InputSetChatroomBlacklist(chatroomId, userId, status, 0);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取聊天室黑名单
     * @param chatroomId 聊天室ID
     * @return 黑名单信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputChatroomBlackInfos> getChatroomBlacklist(String chatroomId) throws Exception {
        String path = APIPath.Chatroom_GetBlacklist;
        InputChatroomId input = new InputChatroomId(chatroomId);
        return AdminHttpUtils.httpJsonPost(path, input, OutputChatroomBlackInfos.class);
    }

    /**
     * 设置聊天室管理员
     * <p>status：1-设置为管理员；0-取消管理员</p>
     * @param chatroomId 聊天室ID
     * @param userId 用户ID
     * @param status 状态值
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setChatroomManager(String chatroomId, String userId, int status) throws Exception {
        String path = APIPath.Chatroom_SetManager;
        InputSetChatroomManager input = new InputSetChatroomManager(chatroomId, userId, status);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取聊天室管理员列表
     * @param chatroomId 聊天室ID
     * @return 管理员ID列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputStringList> getChatroomManagerList(String chatroomId) throws Exception {
        String path = APIPath.Chatroom_GetManagerList;
        InputChatroomId input = new InputChatroomId(chatroomId);
        return AdminHttpUtils.httpJsonPost(path, input, OutputStringList.class);
    }

    /**
     * 设置聊天室全员禁言
     * @param chatroomId 聊天室ID
     * @param mute true-全员禁言，false-取消全员禁言
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setChatroomMute(String chatroomId, boolean mute) throws Exception {
        String path = APIPath.Chatroom_MuteAll;
        InputChatroomMute input = new InputChatroomMute(chatroomId, mute ? 1 : 0);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

}

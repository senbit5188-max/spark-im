package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

/**
 * 好友关系管理类
 * <p>
 * 提供好友关系管理相关的功能，包括：
 * <ul>
 * <li>设置和获取好友关系</li>
 * <li>黑名单管理</li>
 * <li>好友别名和额外信息管理</li>
 * <li>好友请求发送</li>
 * <li>关系查询</li>
 * </ul>
 * </p>
 */
public class RelationAdmin {
    /**
     * 设置用户好友关系
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @param isFriend true-设置为好友，false-删除好友
     * @param extra 额外信息
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setUserFriend(String userId, String targetId, boolean isFriend, String extra) throws Exception {
        String path = APIPath.Friend_Update_Status;
        InputUpdateFriendStatusRequest input = new InputUpdateFriendStatusRequest();
        input.setUserId(userId);
        input.setFriendUid(targetId);
        input.setStatus(isFriend ? 0 : 1); //历史遗留问题，在IM数据库中0是好友，1是好友被删除。
        input.setExtra(extra);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取好友列表
     * @param userId 用户ID
     * @return 好友ID列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputStringList> getFriendList(String userId) throws Exception {
        String path = APIPath.Friend_Get_List;
        InputUserId input = new InputUserId();
        input.setUserId(userId);
        return AdminHttpUtils.httpJsonPost(path, input, OutputStringList.class);
    }

    /**
     * 设置黑名单
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @param isBlacklist true-加入黑名单，false-移出黑名单
     * @return 设置结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> setUserBlacklist(String userId, String targetId, boolean isBlacklist) throws Exception {
        String path = APIPath.Blacklist_Update_Status;
        InputBlacklistRequest input = new InputBlacklistRequest();
        input.setUserId(userId);
        input.setTargetUid(targetId);
        input.setStatus(isBlacklist ? 2 : 1);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取用户黑名单
     * @param userId 用户ID
     * @return 黑名单用户ID列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputStringList> getUserBlacklist(String userId) throws Exception {
        String path = APIPath.Blacklist_Get_List;
        InputUserId input = new InputUserId();
        input.setUserId(userId);
        return AdminHttpUtils.httpJsonPost(path, input, OutputStringList.class);
    }

    /**
     * 更新好友别名
     * @param operator 操作者用户ID
     * @param targetId 目标用户ID
     * @param alias 别名
     * @return 更新结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> updateFriendAlias(String operator, String targetId, String alias) throws Exception {
        String path = APIPath.Friend_Set_Alias;
        InputUpdateAlias input = new InputUpdateAlias();
        input.setOperator(operator);
        input.setTargetId(targetId);
        input.setAlias(alias);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取好友别名
     * @param operator 操作者用户ID
     * @param targetId 目标用户ID
     * @return 好友别名
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputGetAlias> getFriendAlias(String operator, String targetId) throws Exception {
        String path = APIPath.Friend_Get_Alias;
        InputGetAlias input = new InputGetAlias();
        input.setOperator(operator);
        input.setTargetId(targetId);
        return AdminHttpUtils.httpJsonPost(path, input, OutputGetAlias.class);
    }

    /**
     * 更新好友额外信息
     * @param operator 操作者用户ID
     * @param targetId 目标用户ID
     * @param extra 额外信息
     * @return 更新结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> updateFriendExtra(String operator, String targetId, String extra) throws Exception {
        String path = APIPath.Friend_Set_Extra;
        InputUpdateFriendExtra input = new InputUpdateFriendExtra();
        input.setOperator(operator);
        input.setTargetId(targetId);
        input.setExtra(extra);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 发送好友请求
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @param reason 申请理由
     * @param force 是否强制添加（直接成为好友无需对方同意）
     * @return 发送结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> sendFriendRequest(String userId, String targetId, String reason, boolean force) throws Exception {
        String path = APIPath.Friend_Send_Request;
        InputAddFriendRequest input = new InputAddFriendRequest();
        input.setUserId(userId);
        input.setFriendUid(targetId);
        input.setReason(reason);
        input.setForce(force);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取两个用户之间的关系
     * @param userId 用户1的ID
     * @param targetId 用户2的ID
     * @return 关系信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<RelationPojo> getRelation(String userId, String targetId) throws Exception {
        String path = APIPath.Relation_Get;
        StringPairPojo input = new StringPairPojo(userId, targetId);
        return AdminHttpUtils.httpJsonPost(path, input, RelationPojo.class);
    }
}

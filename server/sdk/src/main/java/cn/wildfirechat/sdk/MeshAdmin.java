package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.pojos.mesh.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

import java.util.List;

/**
 * Mesh服务管理类
 * <p>
 * 提供Mesh网络（多域互联）服务管理相关的功能，包括：
 * <ul>
 * <li>域管理（创建、删除、查询）</li>
 * <li>跨域用户搜索</li>
 * <li>跨域好友管理</li>
 * <li>跨域消息发送</li>
 * <li>跨域群组管理</li>
 * <li>会议控制</li>
 * </ul>
 * </p>
 */
public class MeshAdmin {
    /**
     * 创建域
     * @param domainInfo 域信息
     * @return 创建结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> createDomain(InputOutputDomainInfo domainInfo) throws Exception {
        String path = APIPath.Create_Domain;
        return AdminHttpUtils.httpJsonPost(path, domainInfo, Void.class);
    }

    /**
     * 获取域信息
     * @param domainId 域ID
     * @return 域信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<InputOutputDomainInfo> getDomain(String domainId) throws Exception {
        String path = APIPath.Get_Domain;
        InputStringValue inputId = new InputStringValue();
        inputId.setValue(domainId);
        return AdminHttpUtils.httpJsonPost(path, inputId, InputOutputDomainInfo.class);
    }

    /**
     * 删除域
     * @param domainId 域ID
     * @return 删除结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> deleteDomain(String domainId) throws Exception {
        String path = APIPath.Destroy_Domain;
        InputStringValue inputId = new InputStringValue();
        inputId.setValue(domainId);
        return AdminHttpUtils.httpJsonPost(path, inputId, Void.class);
    }

    /**
     * 获取所有域列表
     * @return 域信息列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<InputOutputDomainInfoList> getAllDomain() throws Exception {
        String path = APIPath.List_Domain;
        return AdminHttpUtils.httpJsonPost(path, null, InputOutputDomainInfoList.class);
    }

    /**
     * 批量获取用户信息
     * @param userIds 用户ID列表
     * @return 用户信息列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputUserInfoList> getBatchUserInfos(List<String> userIds) throws Exception {
        String path = APIPath.User_Batch_Get_Infos;
        OutputStringList getUserInfo = new OutputStringList(userIds);
        return AdminHttpUtils.httpJsonPost(path, getUserInfo, OutputUserInfoList.class);
    }

    /**
     * 搜索用户
     * @param keyword 搜索关键词
     * @param searchType 搜索类型
     * @param userType 用户类型
     * @param page 页码
     * @return 搜索结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<PojoSearchUserRes> searchUser(String keyword, int searchType, int userType, int page) throws Exception {
        String path = APIPath.Search_User;
        PojoSearchUserReq req = new PojoSearchUserReq();
        req.keyword = keyword;
        req.searchType = searchType;
        req.page = page;
        req.userType = userType;
        return AdminHttpUtils.httpJsonPost(path, req, PojoSearchUserRes.class);
    }

    /**
     * 发送跨域好友请求
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @param reason 申请理由
     * @return 发送结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> sendFriendRequest(String userId, String targetId, String reason) throws Exception {
        String path = APIPath.Friend_Send_Request;
        InputAddFriendRequest input = new InputAddFriendRequest();
        input.setUserId(userId);
        input.setFriendUid(targetId);
        input.setReason(reason);
        input.setForce(false);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 处理跨域好友请求
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @param status 状态
     * @return 处理结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> handleFriendRequest(String userId, String targetId, int status) throws Exception {
        String path = APIPath.Handle_Friend_Send_Request;
        InputHandleFriendRequest input = new InputHandleFriendRequest();
        input.setUserId(userId);
        input.setFriendUid(targetId);
        input.setStatus(status);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 发送跨域消息
     * @param sender 发送者用户ID
     * @param conversation 会话信息
     * @param payload 消息内容
     * @param toUsers 接收用户ID列表
     * @return 发送结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<SendMessageResult> sendMessage(String sender, Conversation conversation, MessagePayload payload, List<String> toUsers) throws Exception {
        String path = APIPath.Msg_Send;
        SendMessageData messageData = new SendMessageData();
        messageData.setSender(sender);
        messageData.setConv(conversation);
        messageData.setPayload(payload);
        messageData.setToUsers(toUsers);
        messageData.setMeshMessage(true);
        if (payload.getType() == 1 && (payload.getSearchableContent() == null || payload.getSearchableContent().isEmpty())) {
            System.out.println("Payload错误，Payload格式应该跟客户端消息encode出来的Payload对齐，这样客户端才能正确识别。比如文本消息，文本需要放到searchableContent属性。请与客户端同事确认Payload的格式，或则去 https://gitee.com/wfchat/android-chat/tree/master/client/src/main/java/cn/wildfirechat/message 找到消息encode的实现方法！");
        }
        return AdminHttpUtils.httpJsonPost(path, messageData, SendMessageResult.class);
    }

    /**
     * 发布跨域消息
     * @param messageData 消息数据
     * @param receivers 接收者ID列表
     * @param messageId 消息ID
     * @return 发布结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<SendMessageResult> publishMessage(SendMessageData messageData, List<String> receivers, long messageId) throws Exception {
        String path = APIPath.Msg_Publish;
        PojoPublishMessageReq req = new PojoPublishMessageReq();
        req.messageData = messageData;
        req.receivers = receivers;
        req.messageId = messageId;
        return AdminHttpUtils.httpJsonPost(path, req, SendMessageResult.class);
    }

    /**
     * 撤回跨域消息
     * @param operator 操作者用户ID
     * @param messageId 消息ID
     * @return 撤回结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<String> recallMessage(String operator, long messageId) throws Exception {
        String path = APIPath.Msg_Recall;
        RecallMessageData req = new RecallMessageData();
        req.setOperator(operator);
        req.setMessageUid(messageId);
        req.setUserRecall(true);
        return AdminHttpUtils.httpJsonPost(path, req, String.class);
    }

    /**
     * 更新跨域消息内容
     * @param operator 操作者用户ID
     * @param messageUid 消息UID
     * @param payload 新的消息内容
     * @param distribute 是否分发
     * @return 更新结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> updateMessageContent(String operator, long messageUid, MessagePayload payload, boolean distribute) throws Exception {
        String path = APIPath.Msg_Update;
        UpdateMessageContentData updateMessageContentData = new UpdateMessageContentData();
        updateMessageContentData.setOperator(operator);
        updateMessageContentData.setMessageUid(messageUid);
        updateMessageContentData.setPayload(payload);
        updateMessageContentData.setDistribute(distribute?1:0);
        updateMessageContentData.setUpdateTimestamp(0);
        updateMessageContentData.setMeshLocal(1);
        return AdminHttpUtils.httpJsonPost(path, updateMessageContentData, Void.class);
    }

    /**
     * 同步跨域群组
     * @param group_info 群组信息
     * @param members 成员列表
     * @return 同步结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> syncGroup(PojoGroupInfo group_info, List<PojoGroupMember> members) throws Exception {
        String path = APIPath.Sync_Group;
        PojoGroup pojoGroup = new PojoGroup();
        pojoGroup.setGroup_info(group_info);
        pojoGroup.setMembers(members);

        return AdminHttpUtils.httpJsonPost(path, pojoGroup, Void.class);
    }

    /**
     * 获取跨域群组信息
     * @param groupId 群组ID
     * @return 群组信息
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<PojoGroupInfo> getGroupInfo(String groupId) throws Exception {
        return GroupAdmin.getGroupInfo(groupId);
    }

    /**
     * 获取跨域群组成员列表
     * @param groupId 群组ID
     * @return 群组成员列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputGroupMemberList> getGroupMembers(String groupId) throws Exception {
        return GroupAdmin.getGroupMembers(groupId);
    }

    /**
     * 添加跨域群组成员
     * @param operator 操作者用户ID
     * @param groupId 群组ID
     * @param groupMembers 成员列表
     * @param to_lines 消息同步到的线路列表
     * @param notify_message 通知消息
     * @return 添加结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> addGroupMembers(String operator, String groupId, List<PojoGroupMember> groupMembers, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Member_Add;
        InputAddGroupMember addGroupMember = new InputAddGroupMember();
        addGroupMember.setGroup_id(groupId);
        addGroupMember.setMembers(groupMembers);
        addGroupMember.setOperator(operator);
        addGroupMember.setTo_lines(to_lines);
        addGroupMember.setNotify_message(notify_message);
        addGroupMember.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, addGroupMember, Void.class);
    }

    /**
     * 添加跨域加群申请
     * @param operator 操作者用户ID
     * @param groupId 群组ID
     * @param userIds 用户ID列表
     * @param reason 理由
     * @param extra 额外信息
     * @return 添加结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> addJoinGroupRequest(String operator, String groupId, List<String> userIds, String reason, String extra) throws Exception {
        String path = APIPath.Group_Join_Request_Add;
        PojoAddJoinGroupRequest addGroupMember = new PojoAddJoinGroupRequest();
        addGroupMember.operator = operator;
        addGroupMember.group_id = groupId;
        addGroupMember.userIds = userIds;
        addGroupMember.reason = reason;
        addGroupMember.extra = extra;
        return AdminHttpUtils.httpJsonPost(path, addGroupMember, Void.class);
    }

    /**
     * 批量获取跨域群组信息
     * @param groupIds 群组ID列表
     * @return 群组信息列表
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<PojoGroupInfoList> batchGroupInfos(List<String> groupIds) throws Exception {
        return GroupAdmin.batchGroupInfos(groupIds);
    }

    /**
     * 退出跨域群组
     * @param operator 操作者用户ID
     * @param groupId 群组ID
     * @param to_lines 消息同步到的线路列表
     * @param notify_message 通知消息
     * @return 退出结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> quitGroup(String operator, String groupId, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Member_Quit;
        InputQuitGroup quitGroup = new InputQuitGroup();
        quitGroup.setGroup_id(groupId);
        quitGroup.setOperator(operator);
        quitGroup.setTo_lines(to_lines);
        quitGroup.setNotify_message(notify_message);
        quitGroup.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, quitGroup, Void.class);
    }

    /**
     * 解散跨域群组
     * @param operator 操作者用户ID
     * @param groupId 群组ID
     * @param to_lines 消息同步到的线路列表
     * @param notify_message 通知消息
     * @return 解散结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> dismissGroup(String operator, String groupId, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Dismiss;
        InputDismissGroup dismissGroup = new InputDismissGroup();
        dismissGroup.setOperator(operator);
        dismissGroup.setGroup_id(groupId);
        dismissGroup.setTo_lines(to_lines);
        dismissGroup.setNotify_message(notify_message);
        dismissGroup.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, dismissGroup, Void.class);
    }

    /**
     * 踢出跨域群组成员
     * @param operator 操作者用户ID
     * @param groupId 群组ID
     * @param groupMemberIds 成员用户ID列表
     * @param to_lines 消息同步到的线路列表
     * @param notify_message 通知消息
     * @return 踢出结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> kickoffGroupMembers(String operator, String groupId, List<String> groupMemberIds, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Member_Kickoff;
        InputKickoffGroupMember kickoffGroupMember = new InputKickoffGroupMember();
        kickoffGroupMember.setGroup_id(groupId);
        kickoffGroupMember.setMembers(groupMemberIds);
        kickoffGroupMember.setOperator(operator);
        kickoffGroupMember.setTo_lines(to_lines);
        kickoffGroupMember.setMeshMessage(true);
        kickoffGroupMember.setNotify_message(notify_message);
        return AdminHttpUtils.httpJsonPost(path, kickoffGroupMember, Void.class);
    }

    /**
     * 转让跨域群组
     * @param operator 操作者用户ID
     * @param groupId 群组ID
     * @param newOwner 新群主用户ID
     * @param to_lines 消息同步到的线路列表
     * @param notify_message 通知消息
     * @return 转让结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> transferGroup(String operator, String groupId, String newOwner, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Transfer;
        InputTransferGroup transferGroup = new InputTransferGroup();
        transferGroup.setGroup_id(groupId);
        transferGroup.setNew_owner(newOwner);
        transferGroup.setOperator(operator);
        transferGroup.setTo_lines(to_lines);
        transferGroup.setNotify_message(notify_message);
        transferGroup.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, transferGroup, Void.class);
    }

    /**
     * 修改跨域群组信息
     * @param operator 操作者用户ID
     * @param groupId 群组ID
     * @param type 修改信息类型（ModifyGroupInfoType）
     * @param value 新值
     * @param to_lines 消息同步到的线路列表
     * @param notify_message 通知消息
     * @return 修改结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> modifyGroupInfo(String operator, String groupId, /*ModifyGroupInfoType*/int type, String value, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Modify_Info;
        InputModifyGroupInfo modifyGroupInfo = new InputModifyGroupInfo();
        modifyGroupInfo.setGroup_id(groupId);
        modifyGroupInfo.setOperator(operator);
        modifyGroupInfo.setTo_lines(to_lines);
        modifyGroupInfo.setType(type);
        modifyGroupInfo.setValue(value);
        modifyGroupInfo.setNotify_message(notify_message);
        modifyGroupInfo.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, modifyGroupInfo, Void.class);
    }

    /**
     * 用户会议请求
     * @param clientID 客户端ID
     * @param fromUser 发起用户ID
     * @param request 请求内容
     * @param sessionId 会话ID
     * @param roomId 房间ID
     * @param data 数据
     * @param advanced 是否高级模式
     * @return 会议响应
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<PojoUserConferenceResponse> userConferenceRequest(String clientID, String fromUser, String request, long sessionId, String roomId, String data, boolean advanced) throws Exception {
        String path = APIPath.Conference_User_Request;
        PojoUserConferenceRequest conferenceRequest = new PojoUserConferenceRequest();
        conferenceRequest.clientID = clientID;
        conferenceRequest.fromUser = fromUser;
        conferenceRequest.request = request;
        conferenceRequest.sessionId = sessionId;
        conferenceRequest.roomId = roomId;
        conferenceRequest.data = data;
        conferenceRequest.advanced = advanced;
        return AdminHttpUtils.httpJsonPost(path, conferenceRequest, PojoUserConferenceResponse.class);
    }

    /**
     * 用户会议事件
     * @param data 事件数据
     * @param userId 用户ID
     * @param clientId 客户端ID
     * @param isRobot 是否为机器人
     * @return 事件处理结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> userConferenceEvent(String data, String userId, String clientId, boolean isRobot) throws Exception {
        String path = APIPath.Conference_User_Event;
        PojoUserConferenceEvent event = new PojoUserConferenceEvent();
        event.data = data;
        event.userId = userId;
        event.clientId = clientId;
        event.isRobot = isRobot;
        return AdminHttpUtils.httpJsonPost(path, event, Void.class);
    }

    /**
     * Ping域
     * @param domainId 域ID
     * @return Ping响应
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<PojoDomainPingResponse> pingDomain(String domainId) throws Exception {
        String path = APIPath.Ping_Domain;
        PojoDomainPingRequest request = new PojoDomainPingRequest();
        request.domainId = domainId;
        return AdminHttpUtils.httpJsonPost(path, request, PojoDomainPingResponse.class);
    }
}

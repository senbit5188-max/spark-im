package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息管理类
 * <p>
 * 提供消息管理相关的功能，包括：
 * <ul>
 * <li>发送消息（单聊、群聊、聊天室等）</li>
 * <li>撤回消息</li>
 * <li>删除消息</li>
 * <li>更新消息内容</li>
 * <li>广播和群发消息</li>
 * <li>会话管理</li>
 * <li>消息已读和投递状态查询</li>
 * </ul>
 * </p>
 */
public class MessageAdmin {
    /**
     * 发送消息
     * @param sender 发送者用户ID
     * @param conversation 会话信息
     * @param payload 消息内容
     * @return 发送结果，包含消息ID
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<SendMessageResult> sendMessage(String sender, Conversation conversation, MessagePayload payload) throws Exception {
        return sendMessage(sender, conversation, payload, null);
    }

    /**
     * 发送消息（可指定部分接收用户）
     * <p>
     * toUsers为发送给会话中部分用户用的，正常为null，仅当需要指定群/频道/聊天室中部分接收用户时使用
     * </p>
     * @param sender 发送者用户ID
     * @param conversation 会话信息
     * @param payload 消息内容
     * @param toUsers 接收用户ID列表，null表示发送给会话中所有用户
     * @return 发送结果，包含消息ID
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<SendMessageResult> sendMessage(String sender, Conversation conversation, MessagePayload payload, List<String> toUsers) throws Exception {
        return sendMessage(sender, conversation, payload, toUsers, false);
    }

    /**
     * 发送消息（完整参数版本）
     * <p>
     * toUsers为发送给会话中部分用户用的，正常为null，仅当需要指定群/频道/聊天室中部分接收用户时使用
     * </p>
     * @param sender 发送者用户ID
     * @param conversation 会话信息
     * @param payload 消息内容
     * @param toUsers 接收用户ID列表，null表示发送给会话中所有用户
     * @param isUserMessage 是否为用户消息
     * @return 发送结果，包含消息ID
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<SendMessageResult> sendMessage(String sender, Conversation conversation, MessagePayload payload, List<String> toUsers, boolean isUserMessage) throws Exception {
        String path = APIPath.Msg_Send;
        SendMessageData messageData = new SendMessageData();
        messageData.setSender(sender);
        messageData.setConv(conversation);
        messageData.setPayload(payload);
        messageData.setToUsers(toUsers);
        messageData.setUserMessage(isUserMessage);
        if (payload.getType() == 1 && (payload.getSearchableContent() == null || payload.getSearchableContent().isEmpty())) {
            System.out.println("Payload错误，Payload格式应该跟客户端消息encode出来的Payload对齐，这样客户端才能正确识别。比如文本消息，文本需要放到searchableContent属性。请与客户端同事确认Payload的格式，或则去 https://gitee.com/wfchat/android-chat/tree/master/client/src/main/java/cn/wildfirechat/message 找到消息encode的实现方法！");
        }
        return AdminHttpUtils.httpJsonPost(path, messageData, SendMessageResult.class);
    }

    /**
     * 撤回消息
     * @param operator 操作者用户ID
     * @param messageUid 消息UID
     * @return 撤回结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<String> recallMessage(String operator, long messageUid) throws Exception {
        String path = APIPath.Msg_Recall;
        RecallMessageData messageData = new RecallMessageData();
        messageData.setOperator(operator);
        messageData.setMessageUid(messageUid);
        return AdminHttpUtils.httpJsonPost(path, messageData, String.class);
    }

    /**
     * 删除消息（仅专业版支持）
     * @param messageUid 消息UID
     * @return 删除结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> deleteMessage(long messageUid) throws Exception {
        String path = APIPath.Msg_Delete;
        DeleteMessageData deleteMessageData = new DeleteMessageData();
        deleteMessageData.setMessageUid(messageUid);
        return AdminHttpUtils.httpJsonPost(path, deleteMessageData, Void.class);
    }

    /**
     * 清除用户消息（仅专业版支持）
     * @param userId 用户ID
     * @param conversation 会话信息
     * @param fromTime 起始时间
     * @param toTime 结束时间
     * @return 清除结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> clearUserMessages(String userId, Conversation conversation, long fromTime, long toTime) throws Exception {
        String path = APIPath.Msg_Clear_By_User;
        InputClearUserMessages clearUserMessages = new InputClearUserMessages(userId, conversation, fromTime, toTime);
        return AdminHttpUtils.httpJsonPost(path, clearUserMessages, Void.class);
    }

    /**
     * 更新消息内容（仅专业版支持）
     * @param operator 操作者用户ID
     * @param messageUid 消息UID
     * @param payload 新的消息内容
     * @param distribute 是否分发更新
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
        return AdminHttpUtils.httpJsonPost(path, updateMessageContentData, Void.class);
    }

    /**
     * 清除会话（仅专业版支持）
     * @param userId 用户ID
     * @param conversation 会话信息
     * @return 清除结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> clearConversation(String userId, Conversation conversation) throws Exception {
        String path = APIPath.Conversation_Delete;
        InputUserConversation input = new InputUserConversation();
        input.userId = userId;
        input.conversation = conversation;
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    /**
     * 获取单条消息
     * <p>如果想要更多消息的读取，可以直接读取IM服务的数据库</p>
     * @param messageUid 消息UID
     * @return 消息数据
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputMessageData> getMessage(long messageUid) throws Exception {
        String path = APIPath.Msg_GetOne;
        InputMessageUid inputMessageUid = new InputMessageUid(messageUid);
        return AdminHttpUtils.httpJsonPost(path, inputMessageUid, OutputMessageData.class);
    }

    /**
     * 撤回广播消息（仅专业版支持）
     * @param operator 操作者用户ID
     * @param messageUid 消息UID
     * @return 撤回结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> recallBroadCastMessage(String operator, long messageUid) throws Exception {
        String path = APIPath.Msg_RecallBroadCast;
        RecallMessageData messageData = new RecallMessageData();
        messageData.setOperator(operator);
        messageData.setMessageUid(messageUid);
        return AdminHttpUtils.httpJsonPost(path, messageData, Void.class);
    }

    /**
     * 撤回群发消息
     * @param operator 操作者用户ID
     * @param messageUid 消息UID
     * @param receivers 接收者ID列表
     * @return 撤回结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> recallMultiCastMessage(String operator, long messageUid, List<String> receivers) throws Exception {
        String path = APIPath.Msg_RecallMultiCast;
        RecallMultiCastMessageData messageData = new RecallMultiCastMessageData();
        messageData.operator = operator;
        messageData.messageUid = messageUid;
        messageData.receivers = receivers;
        return AdminHttpUtils.httpJsonPost(path, messageData, Void.class);
    }

    /**
     * 删除广播消息（仅专业版支持）
     * @param operator 操作者用户ID
     * @param messageUid 消息UID
     * @return 删除结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> deleteBroadCastMessage(String operator, long messageUid) throws Exception {
        String path = APIPath.Msg_DeleteBroadCast;
        RecallMessageData messageData = new RecallMessageData();
        messageData.setOperator(operator);
        messageData.setMessageUid(messageUid);
        return AdminHttpUtils.httpJsonPost(path, messageData, Void.class);
    }

    /**
     * 删除群发消息
     * @param operator 操作者用户ID
     * @param messageUid 消息UID
     * @param receivers 接收者ID列表
     * @return 删除结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<Void> deleteMultiCastMessage(String operator, long messageUid, List<String> receivers) throws Exception {
        String path = APIPath.Msg_DeleteMultiCast;
        RecallMultiCastMessageData messageData = new RecallMultiCastMessageData();
        messageData.operator = operator;
        messageData.messageUid = messageUid;
        messageData.receivers = receivers;
        return AdminHttpUtils.httpJsonPost(path, messageData, Void.class);
    }

    /**
     * 广播消息（仅专业版支持）
     * @param sender 发送者用户ID
     * @param line 线路
     * @param payload 消息内容
     * @return 广播结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<BroadMessageResult> broadcastMessage(String sender, int line, MessagePayload payload) throws Exception {
        String path = APIPath.Msg_Broadcast;
        BroadMessageData messageData = new BroadMessageData();
        messageData.setSender(sender);
        messageData.setLine(line);
        messageData.setPayload(payload);
        return AdminHttpUtils.httpJsonPost(path, messageData, BroadMessageResult.class);
    }

    /**
     * 群发消息
     * @param sender 发送者用户ID
     * @param receivers 接收者ID列表
     * @param line 线路
     * @param payload 消息内容
     * @return 群发结果
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<MultiMessageResult> multicastMessage(String sender, List<String> receivers, int line, MessagePayload payload) throws Exception {
        String path = APIPath.Msg_Multicast;
        MulticastMessageData messageData = new MulticastMessageData();
        messageData.setSender(sender);
        messageData.setTargets(receivers);
        messageData.setLine(line);
        messageData.setPayload(payload);
        return AdminHttpUtils.httpJsonPost(path, messageData, MultiMessageResult.class);
    }

    /**
     * 获取会话已读时间戳
     * @param userId 用户ID
     * @param conversation 会话信息
     * @return 已读时间戳
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputTimestamp> getConversationReadTimestamp(String userId, Conversation conversation) throws Exception {
        String path = APIPath.Msg_ConvRead;
        InputGetConvReadTime input = new InputGetConvReadTime(userId, conversation.getType(), conversation.getTarget(), conversation.getLine());
        return AdminHttpUtils.httpJsonPost(path, input, OutputTimestamp.class);
    }

    /**
     * 获取消息投递时间戳
     * @param userId 用户ID
     * @return 投递时间戳
     * @throws Exception 请求失败时抛出异常
     */
    public static IMResult<OutputTimestamp> getMessageDelivery(String userId) throws Exception {
        String path = APIPath.Msg_Delivery;
        InputUserId input = new InputUserId(userId);
        return AdminHttpUtils.httpJsonPost(path, input, OutputTimestamp.class);
    }

    /**
     * 导入消息。只用在服务为正式使用之前，从别的IM服务进行导入历史消息。当导入时，只能部署一个节点，可以多线程导入。当导入结束后，检查有没有异常日志，如果有异常日志需要解决后重新导入。
     * 当导入完成后，重启IM服务（不要用kill -9，需要等待缓存写入）。重启之后再用客户端验证是否有历史消息可以从远端加载。另外需要保留之前IM服务的备份记录，以防有消息泄漏。
     * 下面所有参数必须有效才行，如果是群组消息，还需要先创建群组。
     *
     * @param messages 消息列表
     * @return  导入结果
     * @throws Exception
     */
    public static IMResult<Void> importMessage(List<ImportMessagesData.ImportMessage> messages) throws Exception {
        String path = APIPath.Msg_Import;
        ImportMessagesData messageData = new ImportMessagesData();
        messageData.messages = messages;
        return AdminHttpUtils.httpJsonPost(path, messageData, Void.class);
    }
}

package cn.wildfirechat.sdk.model;

import cn.wildfirechat.pojos.Conversation;
import cn.wildfirechat.sdk.messagecontent.MessageContent;

import java.util.List;

/**
 * 消息类
 * <p>
 * 表示一条完整的消息，包含会话信息、消息内容、发送者、时间等。
 * </p>
 */
public class Message {
    public Conversation conversation;
    public MessageContent content;
    public long messageUid;
    public String sender;
    public long serverTime;
    public List<String> toUsers;
}

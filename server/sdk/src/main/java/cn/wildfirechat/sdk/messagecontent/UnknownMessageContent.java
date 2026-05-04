package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

/**
 * 未知消息内容类
 * <p>
 * 表示未知类型的消息内容，用于处理无法识别的消息类型。
 * 保存原始的消息负载以便后续处理。
 * </p>
 */
public class UnknownMessageContent extends MessageContent {
    private MessagePayload orignalPayload;

    //必须有个空的构造函数
    public UnknownMessageContent() {
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Unknown;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        return orignalPayload;
    }

    @Override
    public void decode(MessagePayload payload) {
        this.orignalPayload = payload;
    }
}

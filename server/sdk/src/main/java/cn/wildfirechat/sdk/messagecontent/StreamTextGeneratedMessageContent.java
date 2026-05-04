package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

/**
 * 流式文本已生成消息内容类
 * <p>
 * 表示AI流式文本生成的结果消息。
 * 包含生成的完整文本内容和流ID。
 * </p>
 */
public class StreamTextGeneratedMessageContent extends MessageContent {
    private String text;
    private String streamId;

    //必须有个空的构造函数
    public StreamTextGeneratedMessageContent() {
    }

    public StreamTextGeneratedMessageContent(String text, String streamId) {
        this.text = text;
        this.streamId = streamId;
    }

    public StreamTextGeneratedMessageContent text(String text) {
        this.text = text;
        return this;
    }

    public StreamTextGeneratedMessageContent streamId(String streamId) {
        this.streamId = streamId;
        return this;
    }

    @Override
    public int getContentType() {
            return ProtoConstants.ContentType.StreamingText_Generated;
    }

    @Override
    public int getPersistFlag() {
            return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        this.streamId = payload.getContent();
        this.text = payload.getSearchableContent();
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.setSearchableContent(text);
        payload.setContent(streamId);
        return payload;
    }
}

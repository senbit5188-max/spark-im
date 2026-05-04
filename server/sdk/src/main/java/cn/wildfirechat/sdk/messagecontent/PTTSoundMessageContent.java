/*
 * Copyright (c) 2021 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;


import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import org.json.simple.JSONObject;

/**
 * 对讲语音消息内容类
 * <p>
 * 表示对讲语音类型的消息内容，继承自SoundMessageContent。
 * 用于实时对讲场景。
 * </p>
 */
public class PTTSoundMessageContent extends SoundMessageContent {

    public PTTSoundMessageContent() {
        super();
    }


    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.setSearchableContent("[对讲语音]");

        return payload;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Ptt_Voice;
    }
}

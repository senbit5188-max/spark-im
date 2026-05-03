/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;


import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

/**
 * 提示通知消息内容类
 * <p>
 * 表示提示类型的消息内容，用于显示各种提示信息（如"对方正在输入"等）。
 * </p>
 */
public class TipNotificationMessageContent extends MessageContent {
    public String tip;

    public TipNotificationMessageContent() {
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.setContent(tip);

        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        tip = payload.getContent();
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Tip;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist;
    }
}

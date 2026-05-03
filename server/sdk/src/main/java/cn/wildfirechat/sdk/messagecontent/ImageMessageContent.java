/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;


import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

import java.util.Base64;

/**
 * 图片消息内容类
 * <p>
 * 表示图片类型的消息内容，继承自MediaMessageContent。
 * 包含图片缩略图数据。
 * </p>
 */
public class ImageMessageContent extends MediaMessageContent {
    private byte[] thumbnailBytes;


    public void setThumbnailBytes(byte[] thumbnailBytes) {
        this.thumbnailBytes = thumbnailBytes;
    }

    public byte[] getThumbnailBytes() {
        return thumbnailBytes;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Image;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.setSearchableContent("[图片]");
        if(thumbnailBytes != null) {
            payload.setBase64edData(Base64.getEncoder().encodeToString(thumbnailBytes));
        }

        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        if(payload.getBase64edData() != null) {
            thumbnailBytes = Base64.getDecoder().decode(payload.getBase64edData());
        }
    }

    @Override
    protected int getMediaType() {
        return ProtoConstants.MessageMediaType.IMAGE;
    }
}

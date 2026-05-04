/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Base64;

/**
 * 动态表情消息内容类
 * <p>
 * 表示动态表情（贴纸）类型的消息内容，继承自MediaMessageContent。
 * 包含表情图片的宽高信息。
 * </p>
 */
public class StickerMessageContent extends MediaMessageContent {
    public int width;
    public int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.setSearchableContent("[动态表情]");
        JSONObject objWrite = new JSONObject();
        objWrite.put("x", width);
        objWrite.put("y", height);
        payload.setBase64edData(Base64.getEncoder().encodeToString(objWrite.toString().getBytes()));

        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(payload.getBase64edData())));
            width = (int) jsonObject.get("x");
            height = (int) jsonObject.get("y");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected int getMediaType() {
        return ProtoConstants.MessageMediaType.STICKER;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Sticker;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }
}

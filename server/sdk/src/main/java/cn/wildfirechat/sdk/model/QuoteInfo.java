/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.model;

import org.json.simple.JSONObject;

/**
 * 引用消息信息类
 * <p>
 * 封装被引用消息的信息，包括消息UID、发送者信息、消息摘要等。
 * 用于回复消息时引用原消息内容。
 * </p>
 */
public class QuoteInfo {
    private long messageUid;
    private String userId;
    private String userDisplayName;
    private String messageDigest;

    public long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getMessageDigest() {
        return messageDigest;
    }

    public void setMessageDigest(String messageDigest) {
        this.messageDigest = messageDigest;
    }

    public JSONObject encode() {
        JSONObject object = new JSONObject();
        object.put("u", messageUid);
        object.put("i", userId);
        object.put("n", userDisplayName);
        object.put("d", messageDigest);
        JSONObject quote = new JSONObject();
        quote.put("quote", object);
        return quote;
    }

    public void decode(JSONObject object) {
        JSONObject quote = (JSONObject)object.get("quote");
        if(quote != null) {
            messageUid = (long) quote.get("u");
            userId = (String) quote.get("i");
            userDisplayName = (String) quote.get("n");
            messageDigest = (String) quote.get("d");
        }
    }
}

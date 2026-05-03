package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;

/**
 * 媒体消息内容基类
 * <p>
 * 所有媒体类型消息（图片、语音、视频、文件等）的抽象基类。
 * 包含媒体文件的远程URL信息。
 * </p>
 */
abstract public class MediaMessageContent extends MessageContent {
    private String remoteMediaUrl;

    public String getRemoteMediaUrl() {
        return remoteMediaUrl;
    }

    public void setRemoteMediaUrl(String remoteMediaUrl) {
        this.remoteMediaUrl = remoteMediaUrl;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.setRemoteMediaUrl(remoteMediaUrl);
        payload.setMediaType(getMediaType());
        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        remoteMediaUrl = payload.getRemoteMediaUrl();
    }

    protected abstract int getMediaType();
}

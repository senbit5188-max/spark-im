/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;

import cn.wildfirechat.proto.WFCMessage;
import io.netty.util.internal.StringUtil;

/**
 * 发送消息结果类
 * <p>
 * 封装发送消息后的返回结果，包含消息UID和时间戳。
 * </p>
 */
public class SendMessageResult {
    private long messageUid;
    private long timestamp;

    public SendMessageResult() {
    }

    public SendMessageResult(long messageUid, long timestamp) {
        this.messageUid = messageUid;
        this.timestamp = timestamp;
    }

    public long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

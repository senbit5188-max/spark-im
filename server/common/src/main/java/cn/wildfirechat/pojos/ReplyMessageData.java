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

import java.util.List;

public class ReplyMessageData {
    private long messageUid;
    private MessagePayload payload;
    private boolean only2Sender;

    public long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    public MessagePayload getPayload() {
        return payload;
    }

    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }

    public boolean isOnly2Sender() {
        return only2Sender;
    }

    public void setOnly2Sender(boolean only2Sender) {
        this.only2Sender = only2Sender;
    }

    public static boolean isValide(ReplyMessageData sendMessageData) {
        if(sendMessageData.messageUid <=0  ||
            sendMessageData.getPayload() == null) {
            return false;
        }
        return true;
    }
}

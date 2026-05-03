/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;

import cn.wildfirechat.proto.WFCMessage;

import java.util.List;

public class ImportMessagesData {
    public static class ImportMessage {
        public String sender;
        public Conversation conversation;
        public MessagePayload payload;
        public List<String> receivers;
        public long timestamp;
    }
    public List<ImportMessage> messages;
}

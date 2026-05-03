/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.xiaoleilu.loServer.action.robot;


import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.Conversation;
import cn.wildfirechat.pojos.ReplyMessageData;
import cn.wildfirechat.pojos.SendMessageData;
import cn.wildfirechat.pojos.SendMessageResult;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jetbrains.annotations.NotNull;
import win.liyufan.im.IMTopic;

import java.util.Arrays;

@Route(APIPath.Robot_Message_Reply)
@HttpMethod("POST")
public class ReplyMessageAction extends RobotAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            ReplyMessageData replyMessageData = getRequestBody(request.getNettyRequest(), ReplyMessageData.class);
            if (ReplyMessageData.isValide(replyMessageData)) {
                WFCMessage.Message message = messagesStore.getMessage(replyMessageData.getMessageUid());
                ErrorCode errorCode = null;
                if(message == null) {
                    errorCode = ErrorCode.ERROR_CODE_NOT_EXIST;
                } else if(!message.getContent().getMentionedTargetList().contains(robot.getUid())) {
                    errorCode = ErrorCode.ERROR_CODE_INVALID_MESSAGE;
                } else if(message.getConversation().getType() != ProtoConstants.ConversationType.ConversationType_Private && message.getConversation().getType() != ProtoConstants.ConversationType.ConversationType_Group) {
                    errorCode = ErrorCode.ERROR_CODE_NOT_IMPLEMENT;
                } else if(System.currentTimeMillis() - message.getServerTimestamp() > 15*60*1000) {
                    errorCode = ErrorCode.ERROR_CODE_RECALL_TIME_EXPIRED;
                } else if(!messagesStore.isRobotMentionExternalRobot()) {
                    if(message.getConversation().getType() != ProtoConstants.ConversationType.ConversationType_Group) {
                        errorCode = ErrorCode.ERROR_CODE_NOT_IMPLEMENT;
                    } else {
                        WFCMessage.GroupMember member = messagesStore.getGroupMember(message.getConversation().getTarget(), robot.getUid());
                        if(member == null || member.getType() == ProtoConstants.GroupMemberType.GroupMemberType_Removed) {
                            errorCode = ErrorCode.ERROR_CODE_NOT_IMPLEMENT;
                        }
                    }
                }

                if(errorCode != null) {
                    response.setStatus(HttpResponseStatus.OK);
                    RestResult result = RestResult.resultOf(errorCode);
                    response.setContent(gson.toJson(result));
                    return true;
                }

                SendMessageData sendMessageData = getSendMessageData(message, replyMessageData);
                WFCMessage.Message newMsg = sendMessageData.toProtoMessage().toBuilder().setMessageId(message.getMessageId()).build();
                sendApiRequest(response, IMTopic.RobotReplyMessageTopic, newMsg.toByteArray(), result -> {
                    ByteBuf byteBuf = Unpooled.buffer();
                    byteBuf.writeBytes(result);
                    ErrorCode ec = ErrorCode.fromCode(byteBuf.readByte());
                    if (ec == ErrorCode.ERROR_CODE_SUCCESS) {
                        long messageId = byteBuf.readLong();
                        long timestamp = byteBuf.readLong();
                        sendResponse(response, null, new SendMessageResult(messageId, timestamp));
                    } else {
                        sendResponse(response, ec, null);
                    }
                });
                return false;
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }
        }
        return true;
    }

    private @NotNull SendMessageData getSendMessageData(WFCMessage.Message message, ReplyMessageData replyMessageData) {
        SendMessageData sendMessageData = new SendMessageData();
        sendMessageData.setSender(robot.getUid());
        Conversation conversation = new Conversation();
        conversation.setType(message.getConversation().getType());
        conversation.setLine(message.getConversation().getLine());
        conversation.setTarget(message.getConversation().getTarget());
        sendMessageData.setConv(conversation);
        sendMessageData.setPayload(replyMessageData.getPayload());
        if(replyMessageData.isOnly2Sender()) {
            sendMessageData.setToUsers(Arrays.asList(message.getFromUser()));
        }
        return sendMessageData;
    }
}

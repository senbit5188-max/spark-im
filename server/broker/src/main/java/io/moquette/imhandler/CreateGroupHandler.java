/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.moquette.imhandler;

import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import cn.wildfirechat.pojos.GroupNotificationBinaryContent;
import com.hazelcast.util.StringUtil;
import io.moquette.spi.impl.Qos1PublishHandler;
import io.netty.buffer.ByteBuf;
import cn.wildfirechat.common.ErrorCode;
import win.liyufan.im.IMTopic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Handler(value = IMTopic.CreateGroupTopic)
public class CreateGroupHandler extends GroupHandler<WFCMessage.CreateGroupRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, ProtoConstants.RequestSourceType requestSourceType, WFCMessage.CreateGroupRequest request, Qos1PublishHandler.IMCallback callback) {
        if(request.getGroup().getGroupInfo().getType() < 0 || request.getGroup().getGroupInfo().getType() > 3) {
            return ErrorCode.ERROR_CODE_INVALID_DATA;
        }

        if (!StringUtil.isNullOrEmpty(request.getGroup().getGroupInfo().getTargetId())) {
            WFCMessage.GroupInfo existGroupInfo = m_messagesStore.getGroupInfo(request.getGroup().getGroupInfo().getTargetId());
            if (existGroupInfo != null && existGroupInfo.getDeleted() == 0) {
                return ErrorCode.ERROR_CODE_GROUP_ALREADY_EXIST;
            }
        }
        boolean isAdmin = requestSourceType == ProtoConstants.RequestSourceType.Request_From_Admin;
        if(request.hasNotifyContent() && request.getNotifyContent().getType() > 0 && requestSourceType == ProtoConstants.RequestSourceType.Request_From_User && !m_messagesStore.isAllowClientCustomGroupNotification()) {
            return ErrorCode.ERROR_CODE_NOT_RIGHT;
        }

        if(request.hasNotifyContent() && request.getNotifyContent().getType() > 0 && requestSourceType == ProtoConstants.RequestSourceType.Request_From_Robot && !m_messagesStore.isAllowRobotCustomGroupNotification()) {
            return ErrorCode.ERROR_CODE_NOT_RIGHT;
        }

        if(!isAdmin && request.getGroup().getGroupInfo().getType() == ProtoConstants.GroupType.GroupType_Organization) {
            return ErrorCode.ERROR_CODE_NOT_RIGHT;
        }

        if(requestSourceType == ProtoConstants.RequestSourceType.Request_From_User) {
            int forbiddenClientOperation = m_messagesStore.getGroupForbiddenClientOperation();
            if((forbiddenClientOperation & ProtoConstants.ForbiddenClientGroupOperationMask.Forbidden_Create_Group) > 0) {
                return ErrorCode.ERROR_CODE_NOT_RIGHT;
            }
        }

        Map<String, Integer> failedMembers = new HashMap<>();
        if(requestSourceType == ProtoConstants.RequestSourceType.Request_From_User) {
            ErrorCode errorCode = m_messagesStore.canAddGroupMembers(fromUser, request.getGroup().getMembersList(), failedMembers);
            if (errorCode != ErrorCode.ERROR_CODE_SUCCESS) {
                return errorCode;
            }
        }

        if (request.getGroup().getGroupInfo().getTargetId().length() > 64
            || request.getGroup().getGroupInfo().getName().length() > 64
            || request.getGroup().getGroupInfo().getPortrait().length() > 1024) {
            return ErrorCode.INVALID_PARAMETER;
        }

        if(!isAdmin && !StringUtil.isNullOrEmpty(request.getGroup().getGroupInfo().getName())) {
            if(!m_messagesStore.isSensitiveOnlyMessage()) {
                Set<String> matched = m_messagesStore.handleSensitiveWord(request.getGroup().getGroupInfo().getName());
                if (matched != null && !matched.isEmpty()) {
                    return ErrorCode.ERROR_CODE_SENSITIVE_MATCHED;
                }
            }

            if(!m_messagesStore.isAllowName(request.getGroup().getGroupInfo().getName())) {
                return ErrorCode.ERROR_CODE_NOT_RIGHT;
            }
        }

        WFCMessage.GroupInfo groupInfo = m_messagesStore.createGroup(fromUser, request.getGroup().getGroupInfo(), request.getGroup().getMembersList(), request.getMemberExtra(), isAdmin, failedMembers);
        if (groupInfo != null && groupInfo.getDeleted() == 0) {
            if(request.hasNotifyContent() && request.getNotifyContent().getType() > 0) {
                sendGroupNotification(fromUser, groupInfo.getTargetId(), request.getToLineList(), request.getNotifyContent());
            } else {
                WFCMessage.MessageContent content = new GroupNotificationBinaryContent(groupInfo.getTargetId(), fromUser, groupInfo.getName(), "").setExtra(request.getMemberExtra()).getCreateGroupNotifyContent();
                sendGroupNotification(fromUser, groupInfo.getTargetId(), request.getToLineList(), content);
            }

            if(!failedMembers.isEmpty()) {
                WFCMessage.MessageContent content = new GroupNotificationBinaryContent(groupInfo.getTargetId(), fromUser, null, "").setMi(failedMembers).getGroupNotifyContent(ProtoConstants.MESSAGE_CONTENT_TYPE_REJECT_JOIN_GROUP);
                sendGroupNotification(fromUser, groupInfo.getTargetId(), request.getToLineList(), content);
            }
        }

        byte[] data = groupInfo.getTargetId().getBytes();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        if(failedMembers.isEmpty()) {
            return ErrorCode.ERROR_CODE_SUCCESS;
        } else {
            return ErrorCode.ERROR_CODE_PARTLY_SUCCESS;
        }
    }
}

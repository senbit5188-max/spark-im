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

/**
 * 添加群组成员输入参数类
 * <p>
 * 封装添加群组成员的输入参数，包括群组ID、成员列表、成员额外信息等。
 * </p>
 */
public class InputAddGroupMember extends InputGroupBase {
    private String group_id;
    private List<PojoGroupMember> members;
    private String member_extra;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public List<PojoGroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<PojoGroupMember> members) {
        this.members = members;
    }

    public String getMember_extra() {
        return member_extra;
    }

    public void setMemberExtra(String extra) {
        this.member_extra = extra;
    }

    public boolean isValide() {
        return true;
    }

    public WFCMessage.AddGroupMemberRequest toProtoGroupRequest() {
        WFCMessage.AddGroupMemberRequest.Builder addGroupBuilder = WFCMessage.AddGroupMemberRequest.newBuilder();
        addGroupBuilder.setGroupId(group_id);
        if(!StringUtil.isNullOrEmpty(member_extra)) {
            addGroupBuilder.setExtra(member_extra);
        }
        for (PojoGroupMember pojoGroupMember : getMembers()) {
            WFCMessage.GroupMember.Builder groupMemberBuilder = WFCMessage.GroupMember.newBuilder().setMemberId(pojoGroupMember.getMember_id());
            if (!StringUtil.isNullOrEmpty(pojoGroupMember.getAlias())) {
                groupMemberBuilder.setAlias(pojoGroupMember.getAlias());
            }
            groupMemberBuilder.setType(pojoGroupMember.getType());
            if(!StringUtil.isNullOrEmpty(pojoGroupMember.getExtra())) {
                groupMemberBuilder.setExtra(pojoGroupMember.getExtra());
            }
            addGroupBuilder.addAddedMember(groupMemberBuilder);
        }

        if (to_lines != null) {
            for (Integer line : to_lines
            ) {
                addGroupBuilder.addToLine(line);
            }
        }

        if (notify_message != null) {
            addGroupBuilder.setNotifyContent(notify_message.toProtoMessageContent());
        }
        return addGroupBuilder.build();
    }

}

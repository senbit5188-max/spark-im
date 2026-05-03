package cn.wildfirechat.pojos.mesh;

import java.util.List;

public class PojoAddJoinGroupRequest {
    public String domainId;
    public String operator;
    public String group_id;
    public List<String> userIds;
    public String reason;
    public String extra;
}

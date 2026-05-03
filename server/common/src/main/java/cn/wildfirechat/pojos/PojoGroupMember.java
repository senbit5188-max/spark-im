/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;

/**
 * 群组成员类
 * <p>
 * 封装群组成员的信息，包括：
 * <ul>
 * <li>成员ID</li>
 * <li>群组内别名</li>
 * <li>成员类型</li>
 * <li>扩展信息</li>
 * <li>更新时间和创建时间</li>
 * </ul>
 * </p>
 */
public class PojoGroupMember {
    String member_id;
    String alias;
    int type;
    String extra;
    long updateDt;
    long createDt;

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public long getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(long updateDt) {
        this.updateDt = updateDt;
    }

    public long getCreateDt() {
        return createDt;
    }

    public void setCreateDt(long createDt) {
        this.createDt = createDt;
    }
}

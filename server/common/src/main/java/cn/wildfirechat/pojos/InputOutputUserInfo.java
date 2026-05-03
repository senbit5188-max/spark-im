/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;


import cn.wildfirechat.proto.WFCMessage;

/**
 * 用户信息类
 * <p>
 * 封装用户的所有信息，包括：
 * <ul>
 * <li>用户ID、用户名、显示名称</li>
 * <li>头像、性别</li>
 * <li>手机号、邮箱、地址</li>
 * <li>公司、社交信息</li>
 * <li>用户类型、删除状态</li>
 * <li>更新时间</li>
 * <li>额外扩展信息</li>
 * </ul>
 * </p>
 */
public class InputOutputUserInfo {
    private String userId;
    private String name;
    private String password;
    private String displayName;
    private String portrait;
    private int gender;
    private String mobile;
    private String email;
    private String address;
    private String company;
    private String social;
    private String extra;
    private int type;
    private int deleted;
    private long updateDt;

    public static InputOutputUserInfo fromPbUser(WFCMessage.User pbUser) {
        InputOutputUserInfo inputCreateUser = new InputOutputUserInfo();
        inputCreateUser.userId = pbUser.getUid();
        inputCreateUser.name = pbUser.getName();
        inputCreateUser.displayName = pbUser.getDisplayName();
        inputCreateUser.portrait = pbUser.getPortrait();
        inputCreateUser.gender = pbUser.getGender();
        inputCreateUser.mobile = pbUser.getMobile();
        inputCreateUser.email = pbUser.getEmail();
        inputCreateUser.address = pbUser.getAddress();
        inputCreateUser.company = pbUser.getCompany();
        inputCreateUser.social = pbUser.getSocial();
        inputCreateUser.extra = pbUser.getExtra();
        inputCreateUser.type = pbUser.getType();
        inputCreateUser.updateDt = pbUser.getUpdateDt();
        inputCreateUser.deleted = pbUser.getDeleted();
        return inputCreateUser;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public WFCMessage.User toUser() {
        WFCMessage.User.Builder newUserBuilder = WFCMessage.User.newBuilder()
            .setUid(userId);
        if (name != null)
            newUserBuilder.setName(name);
        if (displayName != null)
            newUserBuilder.setDisplayName(displayName);
        if (getPortrait() != null)
            newUserBuilder.setPortrait(getPortrait());
        if (getEmail() != null)
            newUserBuilder.setEmail(getEmail());
        if (getAddress() != null)
            newUserBuilder.setAddress(getAddress());
        if (getCompany() != null)
            newUserBuilder.setCompany(getCompany());
        if (getSocial() != null)
            newUserBuilder.setSocial(getSocial());

        if (getMobile() != null)
            newUserBuilder.setMobile(getMobile());
        if (getExtra() != null)
            newUserBuilder.setExtra(getExtra());
        newUserBuilder.setGender(gender);
        newUserBuilder.setType(type);
        newUserBuilder.setDeleted(deleted);

        newUserBuilder.setUpdateDt(System.currentTimeMillis());
        return newUserBuilder.build();
    }

    public long getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(long updateDt) {
        this.updateDt = updateDt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "{" +
            "userId='" + userId + '\'' +
            ", name='" + name + '\'' +
            ", displayName='" + displayName + '\'' +
            ", portrait='" + portrait + '\'' +
            ", mobile='" + mobile + '\'' +
            ", updateDt=" + updateDt +
            '}';
    }
}

package cn.wildfirechat.pojos;

import cn.wildfirechat.proto.WFCMessage;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PojoChannelMenu {
    public String menuId;
    public String type;
    public String name;
    public String key;
    public String url;
    public String mediaId;
    public String articleId;
    public String appId;
    public String appPage;
    public String extra;
    public List<PojoChannelMenu> subMenus;

    public static PojoChannelMenu fromPbInfo(WFCMessage.ChannelMenu channelMenuMenu) {
        PojoChannelMenu out = new PojoChannelMenu();
        out.menuId = channelMenuMenu.getMenuId();
        out.type = channelMenuMenu.getType();
        out.name = channelMenuMenu.getName();
        out.key = channelMenuMenu.getKey();
        out.url = channelMenuMenu.getUrl();
        out.mediaId = channelMenuMenu.getMediaId();
        out.articleId = channelMenuMenu.getArticleId();
        out.appId = channelMenuMenu.getAppId();
        out.appPage = channelMenuMenu.getAppPage();
        out.extra = channelMenuMenu.getExtra();
        if (channelMenuMenu.getSubMenuCount() > 0) {
            out.subMenus = new ArrayList<>();
            for (WFCMessage.ChannelMenu menuMenu : channelMenuMenu.getSubMenuList()) {
                out.subMenus.add(fromPbInfo(menuMenu));
            }
        }
        return out;
    }

    public WFCMessage.ChannelMenu.Builder toPbInfo() {
        WFCMessage.ChannelMenu.Builder builder = WFCMessage.ChannelMenu.newBuilder();
        builder.setType(type);
        builder.setName(name);
        if (!StringUtil.isNullOrEmpty(menuId)) builder.setMenuId(menuId);
        if (!StringUtil.isNullOrEmpty(key)) builder.setKey(key);
        if (!StringUtil.isNullOrEmpty(url)) builder.setUrl(url);
        if (!StringUtil.isNullOrEmpty(mediaId)) builder.setMediaId(mediaId);
        if (!StringUtil.isNullOrEmpty(articleId)) builder.setArticleId(articleId);
        if (!StringUtil.isNullOrEmpty(appId)) builder.setAppId(appId);
        if (!StringUtil.isNullOrEmpty(appPage)) builder.setAppPage(appPage);
        if (!StringUtil.isNullOrEmpty(extra)) builder.setExtra(extra);
        if (subMenus != null && !subMenus.isEmpty()) {
            subMenus.forEach(menuMenu -> builder.addSubMenu(menuMenu.toPbInfo()));
        }
        return builder;
    }
}

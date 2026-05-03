# 星火IM (SparkChat)

<p align="center">
  <img src="branding/app_icon.png" width="128" height="128" alt="星火IM Logo">
</p>

<p align="center">
  <strong>一款基于野火IM开源项目二次开发的即时通讯应用</strong>
</p>

---

## 📖 项目简介

星火IM 是一款功能完整的即时通讯解决方案，支持 Android、iOS、Web 等多平台客户端。基于 [wildfirechat](https://github.com/wildfirechat) 开源项目进行品牌定制和功能扩展。

### 主要特性

- **多平台支持**: Android、iOS、Web、PC、小程序
- **功能完整**: 单聊、群聊、音视频通话、朋友圈、频道等
- **部署简单**: 最低 128MB 内存即可运行
- **管理后台**: 内置 Web 管理面板，支持用户、群组、消息管理
- **二次开发**: 基于 MQTT + Protobuf 协议，文档完善

## 📁 项目结构

```
├── server/              # IM 核心服务端 (Java)
├── app-server/          # 应用服务器 - 登录、注册等业务逻辑
├── android-chat/        # Android 客户端
├── ios-chat/            # iOS 客户端
├── admin-panel/         # Web 管理后台
└── branding/            # 品牌资源 (Logo、图标等)
```

## 🚀 快速搭建

### 环境要求

| 依赖 | 版本 | 说明 |
|------|------|------|
| JRE/JDK | 1.8 | 必须使用 Java 8 |
| Maven | 3.6+ | 编译构建用 |
| 内存 | ≥128MB | 服务端最低要求 |
| 系统 | Linux (推荐) | 也支持 macOS/Windows |

### 1. 编译服务端

```bash
# 设置 Java 8 环境
export JAVA_HOME=/path/to/java8

# 编译 IM 服务端
cd server
mvn clean package -DskipTests

# 编译应用服务器
cd ../app-server
mvn clean package -DskipTests
```

### 2. 部署 IM 服务端

```bash
# 解压编译产物
cd server/distribution/target
tar xzf distribution-*-bundle-tar.tar.gz

# 修改配置文件
vim config/wildfirechat.conf
# 修改 server.ip 为您的服务器IP
# 修改 http_port 为您需要的端口（默认80，需要root权限）

# 启动服务
chmod +x bin/wildfirechat.sh
./bin/wildfirechat.sh

# 验证运行
curl http://localhost:8080/api/version
```

### 3. 部署应用服务器

```bash
cd app-server/target
java -jar app-*.jar

# 应用服务器默认端口 8888
```

### 4. 配置客户端

**Android**: 修改 `android-chat/chat/src/main/java/.../Config.java` 中的服务器地址

**iOS**: 修改 `ios-chat/wfchat/WildFireChat/WFCConfig.m` 中的服务器地址

### 5. 管理后台

打开 `admin-panel/index.html`，在"系统配置"中填入服务器地址即可使用。

管理后台功能：
- 仪表盘：实时监控用户数、消息量、在线状态
- 用户管理：查看、搜索、管理用户
- 群组管理：查看和管理群组
- 消息管理：发送系统公告
- 敏感词管理：配置敏感词过滤
- 频道管理：管理公众号/频道
- 服务器状态：监控服务器运行状态
- 系统配置：配置服务器连接参数
- 系统日志：查看运行日志

## ⚙️ 配置说明

### IM 服务端配置 (wildfirechat.conf)

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `server.ip` | 0.0.0.0 | 服务器接入IP |
| `port` | 1883 | MQTT 长连接端口 |
| `http_port` | 80 | 客户端短连接端口 |
| `http.admin.port` | 18080 | 管理API端口 |
| `http.admin.secret_key` | 123456 | 管理API密钥（请修改！） |

### 数据库

- 默认使用 H2 内嵌数据库（适合测试和小规模部署）
- 生产环境建议切换为 MySQL，在 `c3p0-config.xml` 中配置

## 📱 客户端开发

### Android

```bash
# 使用 Android Studio 打开 android-chat 目录
# 修改 app/build.gradle 中的 applicationId
# 修改服务器地址后编译运行
```

### iOS

```bash
# 使用 Xcode 打开 ios-chat/wfchat/WildFireChat.xcworkspace
# 修改 Bundle Identifier
# 修改服务器地址后编译运行
```

## 📝 品牌定制

本项目已完成以下品牌定制：

- 应用名称: 野火IM → **星火**
- 应用图标: 自定义火焰+气泡设计
- 中文/英文/繁体中文 多语言品牌替换
- iOS: Xcode 项目配置、Info.plist、启动画面
- Android: strings.xml、应用名称、关于页面

## ⚠️ 重要提示

1. **不要修改 IM 服务的核心代码**，所有自定义逻辑请在 app-server 中实现
2. 生产环境请务必修改 `http.admin.secret_key`
3. 生产环境建议切换到 MySQL 数据库
4. 管理端口 18080 建议仅内网访问
5. 本项目基于 [wildfirechat](https://github.com/wildfirechat) 开源项目，遵循其开源协议

## 🔗 参考文档

- [野火IM官方文档](https://docs.wildfirechat.cn/)
- [Server API 文档](https://docs.wildfirechat.cn/server/admin_api/)
- [Android SDK 文档](https://docs.wildfirechat.cn/android/)
- [iOS SDK 文档](https://docs.wildfirechat.cn/ios/)

## 📄 许可证

本项目基于野火IM社区版，遵循其开源许可协议。详见各子目录中的 LICENSE 文件。

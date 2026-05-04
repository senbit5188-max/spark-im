# Spark-IM 优化记录

## Web 端 bundle 优化（web-chat）

### 配置改动 (`web-chat/vue.config.js`)

1. **生产环境 strip console**：通过自定义 `terser-webpack-plugin` 配置，
   `drop_console`、`drop_debugger`、`pure_funcs: ['console.log', 'console.info', 'console.debug']`。
2. **gzip 预压缩**：使用 `compression-webpack-plugin` 在 `dist/` 同时输出 `.gz` 文件，
   配合 nginx 的 `gzip_static on` 直接返回，省 CPU 又比即时 gzip 压缩比更高。
3. **更细的 splitChunks**：
   - `chunk-wfc-sdk` —— wildfirechat 协议层 (proto/av/ptt/util)，会被很多页面共享；
   - `chunk-pinyin` —— 3.3MB 的拼音字典走 `chunks: 'async'`，初始包不再拉取；
   - `chunk-vendor-assets` —— `src/vendor/*` 下的其他 lib（lightbox、modal、visibility-change）；
   - `chunk-libs` —— `node_modules` 第三方包；
   - `chunk-commons` —— `src/components` 下被 ≥3 处引用的公共组件。
4. **`maxInitialRequests: 6`** 控制首屏并发请求数，避免分得太碎反而拖慢 HTTP/1.1 客户端。
5. **`productionSourceMap: false`** 已存在，进一步省下 ~30MB 的 sourcemap 体积。

### 体积对比

构建后用 `npm run build:report` 生成 `dist/report.html` 查看分布。

### 已删除的开发期遗留文件

| 文件 | 原大小 | 说明 |
|------|-------:|------|
| `src/assets/fonts/icomoon/demo.html` | 573KB | icomoon 在线生成器导出的演示页，运行时不需要 |
| `src/assets/fonts/icomoon/selection.json` | 632KB | icomoon 项目元数据，仅用于回到 icomoon.io 编辑 |
| `src/assets/fonts/icomoon/demo-files/` | ~3KB | 演示页配套样式 |
| `src/assets/fonts/icomoon/Read Me.txt` | ~1KB | icomoon 说明 |

> 注意：`src/vendor/pinyin/`（3.7MB）、`src/wfc/proto/proto.min.js`（1.1MB）、
> `src/wfc/av/internal/*.min.js`、`src/wfc/ptt/internal/ptt.min.js` **不能直接删**，
> 它们是 wildfirechat 协议 / 音视频 / 拼音排序所需，已通过 splitChunks 拆到独立 chunk。

## Nginx 优化建议

参考 `docs/nginx/spark-im.conf`：
- `gzip_static on` 直接吐出 webpack 预压缩的 `.gz` 文件。
- 静态 hash 资源 `Cache-Control: public, immutable, expires 1y`；
- `index.html` `no-cache, no-store`，保证发布后用户立即拿到新版本。
- admin 站点可以加 `allow/deny` IP 白名单（生产强烈建议）。

## CI/CD 自动部署

工作流：`.github/workflows/deploy.yml`

触发条件：
- `push` 到 `main` / `master` / `temp-main` 且改动落在 `web-chat/`、`landing-page/`、`admin-panel/`、
  `scripts/deploy.sh`、`.github/workflows/deploy.yml` 任一目录；
- 或 GitHub Actions 页面手动 `workflow_dispatch`（可选 target = all/web-chat/landing-page/admin-panel）。

需要在 GitHub 仓库 Settings → Secrets 添加：

| Secret | 说明 |
|--------|------|
| `DEPLOY_HOST` | 服务器 IP（`109.123.239.207`）或域名 |
| `DEPLOY_USER` | SSH 用户名（建议新建专用 `deploy` 账号，不要用 root） |
| `DEPLOY_SSH_KEY` | SSH 私钥（PEM 格式，整段贴入） |
| `DEPLOY_SSH_KNOWN_HOSTS` | （可选）`ssh-keyscan -H <host>` 输出，避免 host key warning |

### 在服务器上准备专用 deploy 账号

```bash
# 1. 新建账号
sudo adduser --disabled-password --gecos "" deploy

# 2. 把 deploy 加进可以写 /opt/spark-im 的组
sudo chgrp -R deploy /opt/spark-im
sudo chmod -R g+rwX /opt/spark-im
sudo find /opt/spark-im -type d -exec chmod g+s {} \;

# 3. 给 deploy 仅 reload nginx 的 sudo 权限
echo 'deploy ALL=(root) NOPASSWD: /usr/bin/systemctl reload nginx, /usr/sbin/nginx -t' \
  | sudo tee /etc/sudoers.d/deploy-nginx
sudo chmod 440 /etc/sudoers.d/deploy-nginx

# 4. 从本机生成 keypair，把公钥放到 deploy 账号
ssh-keygen -t ed25519 -f ~/.ssh/spark-im-deploy -N ''
ssh root@<host> "mkdir -p /home/deploy/.ssh && chown deploy:deploy /home/deploy/.ssh && chmod 700 /home/deploy/.ssh"
ssh-copy-id -i ~/.ssh/spark-im-deploy.pub deploy@<host>

# 5. 把私钥 cat 出来，整段（包括 BEGIN/END）粘到 GitHub Secret DEPLOY_SSH_KEY
cat ~/.ssh/spark-im-deploy
```

## 本地手动部署

```bash
scripts/deploy.sh 109.123.239.207 root           # 用 root 直连
SSH_KEY=~/.ssh/spark-im-deploy scripts/deploy.sh 109.123.239.207 deploy   # 走 deploy 账号
```

## TODO（后续可做）

- [ ] 把 H2 切换到 MySQL（`docs/mysql-migration.md`）
- [ ] admin-panel 加 IP 白名单
- [ ] `wildfirechat.conf` 的 `http.admin.secret_key` 从默认 `123456` 改成强密码
- [ ] `image-minimizer-webpack-plugin` 进一步压 PNG/JPG（需要装 `sharp` 或 `imagemin`）
- [ ] 把 `src/assets/twemoji/64.png`（1.9MB）改成按需 sprite 或 CDN
- [ ] `src/assets/sticker.zip`（3.9MB）按需懒加载

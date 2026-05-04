# MySQL 迁移指南（H2 → MySQL）

生产环境强烈建议把 IM 服务端和 app-server 从默认的 H2（内存/单文件数据库）迁到 MySQL，
原因：
- H2 不适合多实例、热备份、并发写入大的场景。
- 重启进程会丢内存数据；H2 file 模式数据文件如果损坏，没有像 MySQL 那样的工具链可恢复。
- IM server 的 admin API 部分操作（敏感词、群配置等）在 MySQL 下更稳定。

> ⚠️ 这个迁移**不是无损**的：H2 文件里的对话数据、用户登录态在迁移后会丢失。**正式切换前一定先备份并通知用户。**

## 1. 在服务器上安装 MySQL

```bash
sudo apt-get update
sudo apt-get install -y mysql-server
sudo systemctl enable --now mysql
sudo mysql_secure_installation   # 设置 root 密码、移除匿名账户、禁用远程 root 等
```

确认 MySQL 跑在 `127.0.0.1:3306`：

```bash
ss -ltnp | grep 3306
```

## 2. 创建数据库

```sql
CREATE DATABASE wfchat   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;  -- IM server
CREATE DATABASE appdata  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;  -- app-server

CREATE USER 'sparkim'@'localhost' IDENTIFIED BY 'CHANGE_ME_STRONG_PASSWORD';
GRANT ALL PRIVILEGES ON wfchat.*  TO 'sparkim'@'localhost';
GRANT ALL PRIVILEGES ON appdata.* TO 'sparkim'@'localhost';
FLUSH PRIVILEGES;
```

## 3. IM server 切到 MySQL

编辑 `/opt/spark-im/im-server/config/wildfirechat.conf`：

```properties
# 把内置 H2 关掉
# h2db.path ./h2db/wfchat   ← 注释掉或删除

# 启用 c3p0 named-config "mysql"
db.named_config = mysql
```

编辑 `/opt/spark-im/im-server/config/c3p0-config.xml` 的 `mysql` 段，把 `user`/`password`/`jdbcUrl`
改成上一步创建的 `sparkim` 账号。**不要使用默认的 root/123456。**

```xml
<named-config name="mysql">
    <property name="driverClass">com.mysql.cj.jdbc.Driver</property>
    <property name="jdbcUrl">jdbc:mysql://localhost:3306/wfchat?useSSL=false&amp;serverTimezone=GMT%2B8&amp;allowPublicKeyRetrieval=true&amp;useUnicode=true&amp;characterEncoding=UTF-8</property>
    <property name="user">sparkim</property>
    <property name="password">CHANGE_ME_STRONG_PASSWORD</property>
    ...
</named-config>
```

执行 SQL 脚本初始化表结构（IM server 自带）：

```bash
mysql -u sparkim -p wfchat < /opt/spark-im/im-server/sql/wfchat.sql
# 后续 V*.sql 升级脚本按版本号顺序执行
ls /opt/spark-im/im-server/sql/V*.sql | sort | xargs -I{} mysql -u sparkim -p wfchat < {}
```

重启 IM server：

```bash
cd /opt/spark-im/im-server
./bin/wildfirechat.sh stop
./bin/wildfirechat.sh start
tail -f logs/wildfirechat.log
```

## 4. app-server 切到 MySQL

编辑 `/opt/spark-im/app-server/config/application.properties`，把 H2 段注释掉，改用 MySQL：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/appdata?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=sparkim
spring.datasource.password=CHANGE_ME_STRONG_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database=mysql
spring.jpa.hibernate.ddl-auto=update
```

> Hibernate `ddl-auto=update` 会自动建/补齐表，所以 app-server 不需要手动跑 SQL。

重启 app-server：

```bash
sudo systemctl restart spark-im-app-server   # 或对应的 supervisor / nohup 脚本
```

## 5. 验证

- `chat.telvoro.top` 能正常登录（账号 + 密码）
- `admin.telvoro.top` 仪表盘的"在线用户数"刷新得到（admin API 走 18080 → 通到 MySQL）
- `mysql -u sparkim -p wfchat -e 'show tables;'` 看到 `t_user`、`t_message`、`t_group_*` 等表

## 6. 回滚

如果出问题：
1. 停掉 IM server 和 app-server。
2. 把 `wildfirechat.conf` 的 `db.named_config = mysql` 注释掉，恢复 `h2db.path ./h2db/wfchat`。
3. 把 `application.properties` 的 MySQL 段注释掉，恢复 H2 段。
4. 重启服务。

## 7. 备份建议

每天 00:30 跑一次 `mysqldump`，保留 14 天：

```bash
sudo crontab -e
# 加入：
30 0 * * * mysqldump -u sparkim -pYOUR_PWD --single-transaction wfchat appdata | gzip > /var/backups/spark-im-$(date +\%Y\%m\%d).sql.gz && find /var/backups -name 'spark-im-*.sql.gz' -mtime +14 -delete
```

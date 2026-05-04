
DROP TABLE IF EXISTS `t_join_group_request`;
CREATE TABLE `t_join_group_request` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `_uid` varchar(64) NOT NULL,
  `_gid` varchar(64) NOT NULL,
  `_mid` varchar(64) NOT NULL,
  `_request_uid` varchar(64) NOT NULL,
  `_accept_uid` varchar(64) DEFAULT NULL,
  `_reason` TEXT DEFAULT NULL,
  `_extra` TEXT DEFAULT NULL,
  `_status` tinyint NOT NULL DEFAULT 0,
  `_dt` bigint(20) NOT NULL,
  `_read_status` tinyint DEFAULT 0,
  UNIQUE INDEX `join_group_request_key_index` (`_uid` DESC, `_gid` DESC, `_mid` DESC, `_request_uid` DESC),
  INDEX `join_group_request_gm_index` (`_gid` DESC, `_mid` DESC),
  INDEX `join_group_request_sync_index` (`_uid` DESC, `_dt` DESC)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

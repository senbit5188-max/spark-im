DROP INDEX `user_gid_mid_dt_index` ON `t_group_member`;
CREATE INDEX `user_gid_dt_index` ON `t_group_member` (`_gid`, `_dt`);

ALTER TABLE `t_conference` ADD COLUMN `_delete_by` varchar(64) DEFAULT '';
ALTER TABLE `t_conference` ADD COLUMN `_deleted` tinyint DEFAULT 0;
ALTER TABLE `t_conference` ADD COLUMN `_delete_dt` bigint(20) DEFAULT 0;

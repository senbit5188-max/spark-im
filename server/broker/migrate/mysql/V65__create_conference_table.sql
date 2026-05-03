
DROP TABLE IF EXISTS `t_conference`;
CREATE TABLE `t_conference` (
  `_id` varchar(64) NOT NULL PRIMARY KEY,
  `_des` varchar(64) NOT NULL,
  `_pin` varchar(16) NOT NULL,
  `_max_publishers` int(11) NOT NULL DEFAULT 0,
  `_bitrate` int(11) NOT NULL DEFAULT 0,
  `_advance` tinyint NOT NULL DEFAULT 0,
  `_recording` tinyint NOT NULL DEFAULT 0,
  `_create_dt` bigint(20) NOT NULL
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


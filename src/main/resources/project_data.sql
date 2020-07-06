DROP TABLE IF EXISTS `project_data`;
CREATE TABLE `project_data` (
  `id` bigint(20) NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `is_month` bit(1) NOT NULL DEFAULT b'0',
  `value` decimal(19,2) DEFAULT NULL,
  `created` datetime NOT NULL,
  `last_modified` datetime NOT NULL,
  `del_status` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  INDEX `idx_project_id` (`project_id`) USING BTREE,
  INDEX `idx_type` (`type`) USING BTREE,
  INDEX `idx_is_month` (`is_month`) USING BTREE,
  INDEX `idx_value` (`value`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
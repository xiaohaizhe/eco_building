DROP TABLE IF EXISTS `action`;
CREATE TABLE `action` (
  `id` bigint(20) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `action_desc` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `action_ip` varchar(255) DEFAULT NULL,
  `action_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_type` (`type`) USING BTREE,
  INDEX `idx_action_time` (`action_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

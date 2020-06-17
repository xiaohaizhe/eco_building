DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `authority` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `created` datetime NOT NULL,
  `last_modified` datetime NOT NULL,
  `del_status` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  INDEX `idx_name` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
insert into user(id,username,authority,password,created,last_modified)
values(1,'admin','ADMIN','$2a$10$fS/P5vEQiORegbxR54HsjeQtPHTdtB73CZ5p.b/psHml.PrYqXDLS',now(),now());
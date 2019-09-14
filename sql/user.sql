CREATE TABLE `t_user` (
  `id` varchar(45) NOT NULL,
  `name` varchar(10) NOT NULL COMMENT '姓名',
  `gis` geometry NOT NULL COMMENT '空间位置信息',
  `geohash` varchar(20)  GENERATED ALWAYS AS (st_geohash(`gis`,8)) VIRTUAL NOT NULL COMMENT 'geo哈希',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  SPATIAL KEY `idx_gis` (`gis`),
  KEY `idx_geohash` (`geohash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';
DROP DATABASE IF EXISTS `file_parser`;
CREATE DATABASE `file_parser`;
USE `file_parser`;

DROP TABLE IF EXISTS `file_stats`;  

CREATE TABLE `file_stats`(
  `id` VARCHAR(256) NOT NULL, 
  `parser` VARCHAR(256) NOT NULL, 
  `file` VARCHAR(256) NOT NULL, 
  `line` INT(12) NOT NULL, 
  UNIQUE KEY(`id`)
) ENGINE=InnoDB;
/*
Navicat MySQL Data Transfer

Source Server         : 192.168.1.77
Source Server Version : 50636
Source Host           : 192.168.1.77:3306
Source Database       : ssh

Target Server Type    : MYSQL
Target Server Version : 50636
File Encoding         : 65001

Date: 2017-06-20 10:12:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for acct_role
-- ----------------------------
DROP TABLE IF EXISTS `acct_role`;
CREATE TABLE `acct_role` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of acct_role
-- ----------------------------

-- ----------------------------
-- Table structure for acct_user
-- ----------------------------
DROP TABLE IF EXISTS `acct_user`;
CREATE TABLE `acct_user` (
  `id` varchar(36) NOT NULL,
  `nick_name` varchar(255) NOT NULL,
  `register_time` datetime DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of acct_user
-- ----------------------------
INSERT INTO `acct_user` VALUES ('0434a26c-9180-4329-bf90-ac2abe3b903a', 'admin', '2017-04-18 19:40:27', '13022221111');
INSERT INTO `acct_user` VALUES ('16cbbc67-53c0-4893-bfe6-0fbec3d3f0ee', 'admin', '2017-04-18 19:42:51', '13022221111');
INSERT INTO `acct_user` VALUES ('3bc1d05b-6dd4-4ed8-b649-3158cda01eff', 'admin', '2017-04-19 13:57:15', '13022221121');
INSERT INTO `acct_user` VALUES ('9e3b4602-bcc6-447c-beba-470d069cdebd', 'admin', '2017-04-19 13:52:44', '13022221111');
INSERT INTO `acct_user` VALUES ('adcc424f-942d-4145-8e26-ecc56ff92d70', 'admin', '2017-04-19 13:56:30', '13022221121');
INSERT INTO `acct_user` VALUES ('b2eea9ce-9ca0-4638-beb1-4e0424553128', 'andy', '2017-04-18 13:32:10', '13022221111');
INSERT INTO `acct_user` VALUES ('ed4bcd29-01b2-40b0-ac64-e6718c30ca9a', 'admin', '2017-04-19 14:21:04', '13022221121');

-- ----------------------------
-- Table structure for acct_user_role
-- ----------------------------
DROP TABLE IF EXISTS `acct_user_role`;
CREATE TABLE `acct_user_role` (
  `user_id` varchar(36) NOT NULL,
  `role_id` varchar(36) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK_gd0hahfsqct79bsfxx70oli30` (`role_id`),
  CONSTRAINT `FK_fqvt7bddsoq4lc9b7vpwlnbxu` FOREIGN KEY (`user_id`) REFERENCES `acct_user` (`id`),
  CONSTRAINT `FK_gd0hahfsqct79bsfxx70oli30` FOREIGN KEY (`role_id`) REFERENCES `acct_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of acct_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for ssm_user
-- ----------------------------
DROP TABLE IF EXISTS `ssm_user`;
CREATE TABLE `ssm_user` (
  `id` int(11) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `birthday` date DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ssm_user
-- ----------------------------
INSERT INTO `ssm_user` VALUES ('1', 'admin', 'admin', '2017-04-05');

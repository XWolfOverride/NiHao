/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

/*Table structure for table `clids` */

DROP TABLE IF EXISTS `clids`;

CREATE TABLE `clids` (
  `clids` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` char(1) COLLATE utf8_spanish_ci NOT NULL DEFAULT 'I',
  PRIMARY KEY (`clids`),
  UNIQUE KEY `clids_UNIQUE` (`clids`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

/*Data for the table `clids` */

/*Table structure for table `pages` */

DROP TABLE IF EXISTS `pages`;

CREATE TABLE `pages` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(250) COLLATE utf8_spanish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

/*Data for the table `pages` */

/*Table structure for table `qcore_groups` */

DROP TABLE IF EXISTS `qcore_groups`;

CREATE TABLE `qcore_groups` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_spanish_ci NOT NULL,
  `active` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `groups_id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

/*Data for the table `qcore_groups` */

insert  into `qcore_groups`(`id`,`name`,`active`) values (1,'Guest',1);

/*Table structure for table `qcore_rel_usergroups` */

DROP TABLE IF EXISTS `qcore_rel_usergroups`;

CREATE TABLE `qcore_rel_usergroups` (
  `userid` int(10) unsigned NOT NULL,
  `groupid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`userid`,`groupid`),
  UNIQUE KEY `userid_UNIQUE` (`userid`),
  UNIQUE KEY `groupid_UNIQUE` (`groupid`),
  KEY `rel_usergroups_user` (`userid`),
  KEY `rel_usergroups_group` (`groupid`),
  CONSTRAINT `rel_usergroups_group` FOREIGN KEY (`groupid`) REFERENCES `qcore_groups` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `rel_usergroups_user` FOREIGN KEY (`userid`) REFERENCES `qcore_users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

/*Data for the table `qcore_rel_usergroups` */

insert  into `qcore_rel_usergroups`(`userid`,`groupid`) values (1,1);

/*Table structure for table `qcore_users` */

DROP TABLE IF EXISTS `qcore_users`;

CREATE TABLE `qcore_users` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nick` varchar(45) COLLATE utf8_spanish_ci NOT NULL,
  `pwd` char(40) CHARACTER SET ascii NOT NULL,
  `display` varchar(250) COLLATE utf8_spanish_ci DEFAULT NULL,
  `since` datetime NOT NULL,
  `lastaccess` datetime DEFAULT NULL,
  `active` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `qcore_user_id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

/*Data for the table `qcore_users` */

insert  into `qcore_users`(`id`,`nick`,`pwd`,`display`,`since`,`lastaccess`,`active`) values (1,'admin','7110EDA4D09E062AA5E4A390B0A572AC0D2C0220','Administrator','2012-04-10 18:48:35',NULL,1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

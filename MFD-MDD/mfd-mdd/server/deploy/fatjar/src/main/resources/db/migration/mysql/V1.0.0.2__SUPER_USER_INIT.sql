CREATE TABLE IF NOT EXISTS `addresses` (
  `id`            VARCHAR(255) NOT NULL,
  `created_date`  DATETIME     NOT NULL,
  `created_by`    VARCHAR(255) NOT NULL,
  `modified_date` DATETIME     DEFAULT NULL,
  `updated_by`    VARCHAR(255) DEFAULT NULL,
  `city`          VARCHAR(255) NOT NULL,
  `country`       VARCHAR(255) DEFAULT NULL,
  `state`         VARCHAR(255) NOT NULL,
  `street`        VARCHAR(255) NOT NULL,
  `zip_code`      VARCHAR(255) NOT NULL,
  `latitude`      DOUBLE       DEFAULT 0,
  `longitude`     DOUBLE       DEFAULT 0,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `resources` (
  `resource_type` varchar(31) NOT NULL,
  `id` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `content` longblob NOT NULL,
  `content_type` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `contacts` (
  `id`                  VARCHAR(255) NOT NULL,
  `created_date`        DATETIME     NOT NULL,
  `created_by`          VARCHAR(255) NOT NULL,
  `modified_date`       DATETIME     DEFAULT NULL,
  `updated_by`          VARCHAR(255) DEFAULT NULL,
  `email`               VARCHAR(255) NOT NULL,
  `fax`                 VARCHAR(255) DEFAULT NULL,
  `name_first`          VARCHAR(255) NOT NULL,
  `name_last`           VARCHAR(255) NOT NULL,
  `name_middle`         VARCHAR(255) DEFAULT NULL,
  `name_title`          VARCHAR(255) DEFAULT NULL,
  `phone`               VARCHAR(255) NOT NULL,
  `fk_address_id`       VARCHAR(255) NOT NULL,
  `fk_contact_photo_id` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32s98h6qr2v8ro0uyjh07oc75` (`fk_address_id`),
  KEY `FKd91f2ojw7jedovdtse2usa6br` (`fk_contact_photo_id`),
  CONSTRAINT `FK32s98h6qr2v8ro0uyjh07oc75` FOREIGN KEY (`fk_address_id`) REFERENCES `addresses` (`id`),
  CONSTRAINT `FKd91f2ojw7jedovdtse2usa6br` FOREIGN KEY (`fk_contact_photo_id`) REFERENCES `resources` (`id`)
);

CREATE TABLE IF NOT EXISTS `users` (
  `user_type`             VARCHAR(31)  NOT NULL,
  `id`                    VARCHAR(255) NOT NULL,
  `created_date`          DATETIME     NOT NULL,
  `created_by`            VARCHAR(255) NOT NULL,
  `modified_date`         DATETIME      DEFAULT NULL,
  `updated_by`            VARCHAR(255)  DEFAULT NULL,
  `last_activity_date`    DATETIME      DEFAULT NULL,
  `password`              VARCHAR(255)  DEFAULT NULL,
  `status`                VARCHAR(255) NOT NULL,
  `username`              VARCHAR(255) NOT NULL,
  `notifications_enabled` BIT(1)        DEFAULT NULL,
  `fk_contact_id`         VARCHAR(255) NOT NULL,
  `comments`              VARCHAR(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_edk5oe47xn8eylavgvjkfvn1p` (`fk_contact_id`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`),
  CONSTRAINT `FKqv1afnmk8cs5venqy8mf7p76a` FOREIGN KEY (`fk_contact_id`) REFERENCES `contacts` (`id`)
);

INSERT IGNORE INTO addresses
VALUES
  ('d5536a6c-2c80-47e6-8d54-45f938ae12da', NOW(), 'SYSTEM', NOW(), 'SYSTEM', 'Mountain View', 'US', 'CA', '28 Walnut Creek',
   '94597', 0, 0);

INSERT IGNORE INTO contacts
VALUES
  ('b57a5786-51a6-4dda-9e71-2402db3e5f4a', NOW(), 'SYSTEM', NOW(), 'SYSTEM', 'iana@mdd.com', NULL, 'Yana',
                                           'Mayfield', NULL, NULL, '1234567890', 'd5536a6c-2c80-47e6-8d54-45f938ae12da',
   NULL);

INSERT IGNORE INTO users (user_type, id, created_date, created_by, modified_date, updated_by, last_activity_date, password, status, username, notifications_enabled, fk_contact_id, comments)
VALUES
  ('SYSTEM_USER', 'fb409241-68a5-47cd-a379-e356ce403dc0', NOW(), 'SYSTEM', NOW(), 'SYSTEM', NOW(),
                  '$2a$12$xUlX9P9HNF0w7T01UfWihOvuorD6/MGVMhIrApBou/FXiTwCnihPa', 'ACTIVE', 'admin-mdd', TRUE,
   'b57a5786-51a6-4dda-9e71-2402db3e5f4a', 'bootstrapped by script');
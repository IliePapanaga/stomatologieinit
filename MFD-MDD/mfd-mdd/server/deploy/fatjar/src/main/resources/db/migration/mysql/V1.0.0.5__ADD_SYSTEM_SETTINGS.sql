CREATE TABLE IF NOT EXISTS `system_settings` (
  `area` varchar(255) NOT NULL,
  `group` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `encrypted` bit(1) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`area`,`group`,`name`)
);

-- Payments - Prime Rate Merchant's Specifics
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('payments', 'prime_rate_specifics', 'login', 0b0, 'STRING', '');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('payments', 'prime_rate_specifics', 'password', 0b1, 'STRING', '');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('payments', 'prime_rate_specifics', 'api_key', 0b0, 'STRING', '');

-- Payments - Payment attempts
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('payments', 'attempts', 'number_of_attempts', 0b0, 'LONG', '4');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('payments', 'attempts', 'interval_days', 0b0, 'LONG', '5');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('payments', 'attempts', 'ach_penalty_fee', 0b0, 'LONG', '5');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('payments', 'attempts', 'cc_penalty_fee', 0b0, 'LONG', '0');

-- Job fees - Temporary job fees
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('job_fees', 'temporary', 'compensation_rda', 0b0, 'LONG', '50');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('job_fees', 'temporary', 'compensation_rdh', 0b0, 'LONG', '60');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('job_fees', 'temporary', 'compensation_dds', 0b0, 'LONG', '75');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('job_fees', 'temporary', 'compensation_specialist', 0b0, 'LONG', '100');

-- Job fees - Permanent job
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('job_fees', 'permanent', 'weeks_per_year', 0b0, 'LONG', '52');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('job_fees', 'permanent', 'percentage_for_hiring', 0b0, 'LONG', '15');

-- Postings - Default
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('postings', 'default', 'allowed_no_show', 0b0, 'LONG', '2');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('postings', 'default', 'allowed_rejections', 0b0, 'LONG', '5');
INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
  VALUES ('postings', 'default', 'payment_start_after_starting_job', 0b0, 'LONG', '60');
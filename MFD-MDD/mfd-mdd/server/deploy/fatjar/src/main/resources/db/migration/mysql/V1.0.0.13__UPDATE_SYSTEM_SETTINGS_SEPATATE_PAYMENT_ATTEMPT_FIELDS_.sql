INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
VALUES ('payments', 'attempts', 'number_of_attempts_card', 0b0, 'LONG', '4');


UPDATE `system_settings`
set `name` = 'number_of_attempts_ach'
WHERE `area` = 'payments'
  AND `group` = 'attempts'
  AND `name` = 'number_of_attempts'

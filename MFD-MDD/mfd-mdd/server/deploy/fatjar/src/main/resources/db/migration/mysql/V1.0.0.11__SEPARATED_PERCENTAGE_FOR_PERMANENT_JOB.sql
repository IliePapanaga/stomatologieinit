INSERT IGNORE INTO `system_settings` (`area`, `group`, `name`, `encrypted`, `type`, `value`)
VALUES ('job_fees', 'permanent', 'percentage_for_hiring_others', 0b0, 'LONG', '10');

UPDATE `system_settings`
set `name` = 'percentage_for_hiring_dentists'
WHERE `area` = 'job_fees'
  AND `group` = 'permanent'
  AND `name` = 'percentage_for_hiring'
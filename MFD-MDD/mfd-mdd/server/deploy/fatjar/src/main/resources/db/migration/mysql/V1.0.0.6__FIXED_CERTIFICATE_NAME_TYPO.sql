-- disable foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

-- update id field in certificate types table
UPDATE `certificate_types`
  SET `id` = 'DIODE_LASER'
  WHERE `id` = 'DIODE_LASE';

-- update foreign keys
UPDATE IGNORE `subcategory_to_certificate_type`
  SET `fk_certificate_type_id` = 'DIODE_LASER'
  WHERE `fk_certificate_type_id` = 'DIODE_LASE';

-- enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
DROP TABLE IF EXISTS `notification_templates`;

CREATE TABLE IF NOT EXISTS `notification_templates` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255),
  `description` varchar(1000),
  `content` varchar(4000) NOT NULL,
  `subject` varchar(255),
  `type` varchar(255) NOT NULL,
  `transport_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_NOTIFICATION_TEMPLATE` (`type`,`transport_id`),
  KEY `FKhbtyxfuk1dp09knjoiyhegbri` (`transport_id`),
  CONSTRAINT `FKhbtyxfuk1dp09knjoiyhegbri` FOREIGN KEY (`transport_id`) REFERENCES `messaging_transports` (`id`)
);


  INSERT IGNORE INTO notification_templates (id, name, description, content, subject, type, transport_id)
SELECT 'aa2eb38f-db32-4653-878b-55d481e0f447', 'Sign Up', 'Message with verification link for E-Mail','
<html>
<head>
</head>
<body>
Hello, {first.name} {last.name}<br><br>

Welcome to MayDayDental Staffing!<br>
Thank you for joining MayDayDental Staffing.<br>
Please <a href="{main.hyperlink}">verify your email</a> and complete your sign up process.<br>
If you have any questions please contact {mdd.admin.email}<br><br>

Thanks,<br><br>

The MayDayDental Staffing Team<br>
</body>
</html>
', 'Welcome to MDD', 'SIGN_UP',(select id from messaging_transports where name = 'EMAIL');

  INSERT IGNORE INTO notification_templates (id, name, description, content, subject, type, transport_id)
SELECT '607fc9bd-d21c-44df-9d1f-54bda41a6d94', 'Reset password', 'Message with reset password link', '
<html>
<head>
</head>
<body>
Hello, {first.name} {last.name}<br><br>

To reset your MayDayDental Staffing password click the link below.<br>
<a href="{main.hyperlink}">Reset password</a><br>
If you have any questions please contact {mdd.admin.email}<br><br>

Thanks,<br><br>

The MayDayDental Staffing Team<br>
</body>
</html>
', 'Reset Password MDD', 'RESET_PASSWORD',(select id from messaging_transports where name = 'EMAIL');

  INSERT IGNORE INTO notification_templates (id, name, description, content, subject, type, transport_id)
SELECT 'aeccb371-417c-4f65-b513-9523498bbbe0', 'Change username', 'Message with verification link for new username', '
<html>
<head>
</head>
<body>
Hello, {first.name} {last.name}<br><br>

Please confirm your new email address by clicking the link below<br>
<a href="{main.hyperlink}">Change username</a><br>
If you have any questions please contact {mdd.admin.email}<br>
Thanks,<br><br>

The MayDayDental Staffing Team<br>
</body>
</html>
', 'Change MDD username', 'CHANGE_USERNAME',(select id from messaging_transports where name = 'EMAIL');
REPLACE INTO notification_templates (id, content, subject, type, transport_id)
SELECT 'aa2eb38f-db32-4653-878b-55d481e0f447', '
<html>
<head>
</head>
<body>
Hello, {first.name} {last.name}<br><br>

Welcome to MayDayDental Staffing!<br>
Thank you for joining MayDayDental Staffing.<br>
Please <a href="{main.hyperlink}">verify your email</a> and complete your sign up process.<br>
If you have any questions please contact {mdd.admin.email}<br><br>

Thanks,<br>
Mayday Dental Staffing Team<br>
<a href="https://jobs.maydaydentalstaffing.com/">https://jobs.maydaydentalstaffing.com</a><br>
Phone number: (888) 899-4386<br>
</body>
</html>
', 'Welcome to MDD', 'SIGN_UP',(select id from messaging_transports where name = 'EMAIL');

REPLACE INTO notification_templates (id, content, subject, type, transport_id)
SELECT '607fc9bd-d21c-44df-9d1f-54bda41a6d94', '
<html>
<head>
</head>
<body>
Hello, {first.name} {last.name}<br><br>

To reset your MayDayDental Staffing password click the link below.<br>
<a href="{main.hyperlink}">Reset password</a><br>
If you have any questions please contact {mdd.admin.email}<br><br>

Thanks,<br>
Mayday Dental Staffing Team<br>
<a href="https://jobs.maydaydentalstaffing.com/">https://jobs.maydaydentalstaffing.com</a><br>
Phone number: (888) 899-4386<br>
</body>
</html>
', 'Reset Password MDD', 'RESET_PASSWORD',(select id from messaging_transports where name = 'EMAIL');

REPLACE INTO notification_templates (id, content, subject, type, transport_id)
SELECT 'aeccb371-417c-4f65-b513-9523498bbbe0', '
<html>
<head>
</head>
<body>
Hello, {first.name} {last.name}<br><br>

Please confirm your new email address by clicking the link below<br>
<a href="{main.hyperlink}">Change username</a><br>
If you have any questions please contact {mdd.admin.email}<br><br>

Thanks,<br>
Mayday Dental Staffing Team<br>
<a href="https://jobs.maydaydentalstaffing.com/">https://jobs.maydaydentalstaffing.com</a><br>
Phone number: (888) 899-4386<br>
</body>
</html>
', 'Change MDD username', 'CHANGE_USERNAME',(select id from messaging_transports where name = 'EMAIL');
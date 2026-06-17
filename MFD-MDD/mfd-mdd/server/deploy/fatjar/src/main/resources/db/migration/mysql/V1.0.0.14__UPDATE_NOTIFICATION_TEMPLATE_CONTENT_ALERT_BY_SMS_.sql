update `notification_templates`
set `content` = 'Doctor is concerned with you running late. Please respond to Doctor via the Mayday Dental App'
where `type` = 'ALERTED_ATTENDANCE_FOR_EMPLOYEE'
  and `transport_id` = (select `id` from `messaging_transports` where `name` = 'SMS');
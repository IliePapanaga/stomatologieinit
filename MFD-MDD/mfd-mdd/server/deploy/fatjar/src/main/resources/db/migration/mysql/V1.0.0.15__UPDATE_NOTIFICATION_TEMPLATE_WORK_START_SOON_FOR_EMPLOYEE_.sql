update `notification_templates`
set `content` = 'Hi {professional.first.name},

This is the reminder that you are scheduled to work tomorrow ({job.posting.application.start.date}) for Dr. {client.last.name} from {job.posting.application.start.time} to {job.posting.application.end.time}. Please do not be late!
If you have any questions please contact {mdd.admin.email}.

Thanks, Mayday Dental Staffing team '
where `type` = 'WORK_START_SOON_FOR_EMPLOYEE'
  and `transport_id` = (select `id` from `messaging_transports` where `name` = 'EMAIL');
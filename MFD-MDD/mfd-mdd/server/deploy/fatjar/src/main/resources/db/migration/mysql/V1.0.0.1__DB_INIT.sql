--FOR PERSISTENT REMEMBER ME FEATURE (SPRING_SECURITY)
create table if not exists persistent_logins (username varchar(64) not null,
                                              series varchar(64) primary key,
                                              token varchar(64) not null,
                                              last_used timestamp not null);

CREATE TABLE IF NOT EXISTS categories (
  id varchar(255) primary key,
  name varchar(255) NOT NULL);


INSERT IGNORE INTO categories(id, name)
    VALUES
      ('HYGIENISTS', 'Hygienists'),
      ('FRONT_OFFICE_PERSONNEL', 'Front Office Personnel'),
      ('ASSISTANTS', 'Assistants'),
      ('DENTISTS', 'Dentists/Specialists');

CREATE TABLE IF NOT EXISTS `sub_categories` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `fk_category_id` varchar(255) NOT NULL,
PRIMARY KEY (`id`),
KEY `FKrbph5dltl5p8fq0u2pcotssso` (`fk_category_id`),
CONSTRAINT `FKrbph5dltl5p8fq0u2pcotssso` FOREIGN KEY (`fk_category_id`) REFERENCES `categories` (`id`)
);

INSERT IGNORE INTO `sub_categories` VALUES
  ('RDH','RDH','HYGIENISTS'),
  ('RDHAP','RDHAP','HYGIENISTS'),
  ('RDH_LASER','RDH with diode laser certification','HYGIENISTS'),
  ('PATIENT_COORDINATOR','Patient coordinator','FRONT_OFFICE_PERSONNEL'),
  ('REGIONAL_MANAGER','Regional manager','FRONT_OFFICE_PERSONNEL'),
  ('RECEPTIONIST','Receptionist','FRONT_OFFICE_PERSONNEL'),
  ('FINANCIAL_COORDINATOR','Financial coordinator','FRONT_OFFICE_PERSONNEL'),
  ('INSURANCE_COORDINATOR','Insurance coordinator','FRONT_OFFICE_PERSONNEL'),
  ('TREATMENT_COORDINATOR','Treatment coordinator','FRONT_OFFICE_PERSONNEL'),
  ('OFFICE_MANAGER','Office manager','FRONT_OFFICE_PERSONNEL'),
  ('ENDODONTIC_ASSISTANT','Endodontic assistant','ASSISTANTS'),
  ('RDA','RDA','ASSISTANTS'),
  ('RDAEF','RDAEF','ASSISTANTS'),
  ('PEDODONTIC_ASSISTANT','Pedodontic assistant','ASSISTANTS'),
  ('DA','DA','ASSISTANTS'),
  ('RDAEF1','RDAEF1','ASSISTANTS'),
  ('RDAEF2','RDAEF2','ASSISTANTS'),
  ('ORAL_SURGERY_ASSISTANT','Oral surgery assistant','ASSISTANTS'),
  ('ORTHODONTIC_ASSISTANT','Orthodontic assistant','ASSISTANTS'),
  ('PERIODONTAL_ASSISTANT','Periodontal assistant','ASSISTANTS'),
  ('PERIODONTIST','Periodontist','DENTISTS'),
  ('PROSTHODONTIST','Prosthodontist','DENTISTS'),
  ('ENDODONTIST','Endodontist','DENTISTS'),
  ('PEDODONTIST','Pedodontist','DENTISTS'),
  ('COSMETIC_DENTIST','Cosmetic dentist','DENTISTS'),
  ('ORAL_SURGEON','Oral surgeon','DENTISTS'),
  ('ORTHODONTIST','Orthodontist','DENTISTS'),
  ('DENTAL_ANESTHESIOLOGIST','Dental anesthesiologist','DENTISTS'),
  ('GENERAL_DENTIST','General dentist','DENTISTS');


CREATE TABLE `subcategory_comprised_subcategories` (
  `fk_subcategory_id` varchar(255) NOT NULL,
  `fk_comprised_subcategory_id` varchar(255) NOT NULL,
  PRIMARY KEY (`fk_subcategory_id`,`fk_comprised_subcategory_id`),
  KEY `FK7bje64aibbyls37pj27lnly5g` (`fk_comprised_subcategory_id`),
  CONSTRAINT `FK7bje64aibbyls37pj27lnly5g` FOREIGN KEY (`fk_comprised_subcategory_id`) REFERENCES `sub_categories` (`id`),
  CONSTRAINT `FKk830x1un4of57w722d5drwy3m` FOREIGN KEY (`fk_subcategory_id`) REFERENCES `sub_categories` (`id`)
);

INSERT IGNORE INTO `subcategory_comprised_subcategories` VALUES
  ('RDA','DA'),
  ('RDAEF','DA'),
  ('RDAEF','RDA'),
  ('RDAEF1','DA'),
  ('RDAEF1','RDA'),
  ('RDAEF1','RDAEF'),
  ('RDAEF2','DA'),
  ('RDAEF2','RDA'),
  ('RDAEF2','RDAEF');

CREATE TABLE IF NOT EXISTS specialities (
  id varchar(255) primary key,
  name varchar(255) NOT NULL);

INSERT IGNORE INTO specialities(id, name)
VALUES
  ('GENERAL', 'General'),
  ('ORTHODONTICS', 'Orthodontics'),
  ('ORAL_AND_MAX_SURGERY', 'Oral and Maxillofacial Surgery'),
  ('ENDODONTICS', 'Endodontics'),
  ('PEDODONTICS', 'Pedodontics'),
  ('PERIODONTICS', 'Periodontics'),
  ('PROSTHODONTICS', 'Prosthodontics'),
  ('DENTAL_ANESTHESIOLOGY ', 'Dental Anesthesiology ');


CREATE TABLE IF NOT EXISTS certificate_types (
  id varchar(255) primary key,
  optional BIT NOT NULL
  );

INSERT IGNORE INTO certificate_types(id, optional)
VALUES
  ('DAC', TRUE),
  ('XRAY', FALSE),
  ('CDA', TRUE),
  ('CPR', FALSE),
  ('RDA', FALSE),
  ('RDAEF',FALSE),
  ('RDAEF1',FALSE),
  ('RDAEF2',FALSE),
  ('RDH', FALSE),
  ('RDHAP', FALSE),
  ('LIABILITY', FALSE),
  ('DIODE_LASE', TRUE),
  ('DDS_OR_DMD', FALSE),
  ('DEA', FALSE),
  ('NPI', FALSE);

CREATE TABLE IF NOT EXISTS subcategory_to_certificate_type (
  `fk_subcategory_id` varchar(255) NOT NULL,
  `fk_certificate_type_id` varchar(255) NOT NULL,
  PRIMARY KEY (`fk_subcategory_id`,`fk_certificate_type_id`),
  CONSTRAINT `FK91ek8u0xh5clcu5xgd7mr3bed` FOREIGN KEY (`fk_subcategory_id`) REFERENCES `sub_categories` (`id`),
  CONSTRAINT `FK2g0jinmkrqv1kijlq2y9hos6i` FOREIGN KEY (`fk_certificate_type_id`) REFERENCES `certificate_types` (`id`)
  );

INSERT IGNORE INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id)
VALUES
  ('DA', 'DAC'),
  ('DA', 'CPR'),
  ('DA', 'XRAY'),
  ('RDA', 'RDA'),
  ('RDA', 'CDA'),
  ('RDA', 'CPR'),
  ('RDAEF', 'RDA'),
  ('RDAEF', 'CDA'),
  ('RDAEF', 'CPR'),
  ('RDAEF1', 'RDA'),
  ('RDAEF1', 'CDA'),
  ('RDAEF1', 'CPR'),
  ('RDAEF2', 'RDA'),
  ('RDAEF2', 'CDA'),
  ('RDAEF2', 'CPR'),
  ('RDH', 'RDH'),
  ('RDH', 'CPR'),
  ('RDH', 'LIABILITY'),
  ('RDH', 'DIODE_LASE'),
  ('RDHAP', 'RDHAP'),
  ('RDHAP', 'CPR'),
  ('RDHAP', 'LIABILITY'),
  ('RDHAP', 'DIODE_LASE'),
  ('GENERAL_DENTIST', 'DDS_OR_DMD'),
  ('GENERAL_DENTIST', 'CPR'),
  ('GENERAL_DENTIST', 'DEA'),
  ('GENERAL_DENTIST', 'NPI'),
  ('GENERAL_DENTIST', 'LIABILITY'),
  ('COSMETIC_DENTIST', 'DDS_OR_DMD'),
  ('COSMETIC_DENTIST', 'CPR'),
  ('COSMETIC_DENTIST', 'DEA'),
  ('COSMETIC_DENTIST', 'NPI'),
  ('COSMETIC_DENTIST', 'LIABILITY'),
  ('PEDODONTIST', 'DDS_OR_DMD'),
  ('PEDODONTIST', 'CPR'),
  ('PEDODONTIST', 'DEA'),
  ('PEDODONTIST', 'NPI'),
  ('PEDODONTIST', 'LIABILITY'),
  ('PROSTHODONTIST', 'DDS_OR_DMD'),
  ('PROSTHODONTIST', 'CPR'),
  ('PROSTHODONTIST', 'DEA'),
  ('PROSTHODONTIST', 'NPI'),
  ('PROSTHODONTIST', 'LIABILITY'),
  ('PERIODONTIST', 'DDS_OR_DMD'),
  ('PERIODONTIST', 'CPR'),
  ('PERIODONTIST', 'DEA'),
  ('PERIODONTIST', 'NPI'),
  ('PERIODONTIST', 'LIABILITY'),
  ('ORTHODONTIST', 'DDS_OR_DMD'),
  ('ORTHODONTIST', 'CPR'),
  ('ORTHODONTIST', 'DEA'),
  ('ORTHODONTIST', 'NPI'),
  ('ORTHODONTIST', 'LIABILITY'),
  ('ENDODONTIST', 'DDS_OR_DMD'),
  ('ENDODONTIST', 'CPR'),
  ('ENDODONTIST', 'DEA'),
  ('ENDODONTIST', 'NPI'),
  ('ENDODONTIST', 'LIABILITY'),
  ('ORAL_SURGEON', 'DDS_OR_DMD'),
  ('ORAL_SURGEON', 'CPR'),
  ('ORAL_SURGEON', 'DEA'),
  ('ORAL_SURGEON', 'NPI'),
  ('ORAL_SURGEON', 'LIABILITY'),
  ('DENTAL_ANESTHESIOLOGIST', 'DDS_OR_DMD'),
  ('DENTAL_ANESTHESIOLOGIST', 'CPR'),
  ('DENTAL_ANESTHESIOLOGIST', 'DEA'),
  ('DENTAL_ANESTHESIOLOGIST', 'NPI'),
  ('DENTAL_ANESTHESIOLOGIST', 'LIABILITY'),
  ('RDH_LASER','CPR'),
  ('ENDODONTIC_ASSISTANT','CPR'),
  ('PEDODONTIC_ASSISTANT','CPR'),
  ('ORAL_SURGERY_ASSISTANT','CPR'),
  ('ORTHODONTIC_ASSISTANT','CPR'),
  ('PERIODONTAL_ASSISTANT','CPR');

CREATE TABLE IF NOT EXISTS bay_areas (
  id varchar(255) primary key,
  name varchar(255) NOT NULL);

INSERT IGNORE INTO bay_areas(id, name)
VALUES
  ('NB', 'NB'),
  ('EB', 'EB'),
  ('SF', 'SF'),
  ('SB', 'SB'),
  ('SACR', 'SACR'),
  ('PENNIN', 'PENNIN'),
  ('MONTER_SANCRUZ', 'MONTER/SAN CRUZ');

CREATE TABLE IF NOT EXISTS educations (
  id varchar(255) primary key,
  name varchar(255) NOT NULL);

INSERT IGNORE INTO educations(id, name)
VALUES
  ('HIGH_SCHOOL', 'High school or GED'),
  ('COLLEGE', 'College'),
  ('UNIVERSITY', 'University');


CREATE TABLE IF NOT EXISTS academic_degrees (
  id varchar(255) primary key,
  name varchar(255) NOT NULL);

INSERT IGNORE INTO academic_degrees(id, name)
VALUES
  ('HIGH_SCHOOL_DIPLOMA', 'HS Diploma'),
  ('GED', 'GED'),
  ('VOCATIONAL_TRAINING_CERTIFICATE', 'Vocational training certificate'),
  ('AA_AS', 'AA/AS'),
  ('BA', 'BA'),
  ('MASTER', 'Masters'),
  ('PHD', 'PhD');


CREATE TABLE IF NOT EXISTS languages (
  id varchar(255) primary key,
  name varchar(255) NOT NULL);

INSERT IGNORE INTO languages(id, name)
VALUES
('ENGLISH','English'),
('SPANISH','Spanish'),
('CHINESE','Chinese'),
('TAGALOG','Tagalog'),
('RUSSIAN','Russian'),
('VIETNAMESE','Vietnamese'),
('ARABIC','Arabic'),
('JAPANESE','Japanese'),
('FRENCH','French'),
('KOREAN','Korean'),
('HINDI','Hindi'),
('MANDARIN','Mandarin'),
('GERMAN','German'),
('ITALIAN','Italian'),
('FARSI','Farsi');

CREATE TABLE IF NOT EXISTS week_days (
  id varchar(10) primary key,
  name varchar(10) NOT NULL,
  index_number int NOT NULL);


INSERT IGNORE INTO week_days(id, name, index_number)
VALUES
  ('MONDAY', 'Monday', 2),
  ('TUESDAY', 'Tuesday', 3),
  ('WEDNESDAY', 'Wednesday', 4),
  ('THURSDAY', 'Thursday', 5),
  ('FRIDAY', 'Friday', 6),
  ('SATURDAY', 'Saturday', 7),
  ('SUNDAY', 'Sunday', 1);





CREATE TABLE IF NOT EXISTS `messaging_transports` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_TRANSPORT_NAME` (`name`)
);

INSERT IGNORE INTO messaging_transports(id, name)
VALUES
  ('9591c642-e250-4157-8492-ffaf8a6017c9',	'EMAIL'),
  ('12f8ea9a-c93b-475a-a225-85d3e4a57d23',	'SMS');

CREATE TABLE IF NOT EXISTS `notification_templates` (
  `id` varchar(255) NOT NULL,
  `content` varchar(4000) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `transport_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_NOTIFICATION_TEMPLATE` (`type`,`transport_id`),
  KEY `FKhbtyxfuk1dp09knjoiyhegbri` (`transport_id`),
  CONSTRAINT `FKhbtyxfuk1dp09knjoiyhegbri` FOREIGN KEY (`transport_id`) REFERENCES `messaging_transports` (`id`)
);


  INSERT IGNORE INTO notification_templates (id, content, subject, type, transport_id)
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

Thanks,<br><br>

The MayDayDental Staffing Team<br>
</body>
</html>
', 'Welcome to MDD', 'SIGN_UP',(select id from messaging_transports where name = 'EMAIL');

  INSERT IGNORE INTO notification_templates (id, content, subject, type, transport_id)
SELECT '607fc9bd-d21c-44df-9d1f-54bda41a6d94', '
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

  INSERT IGNORE INTO notification_templates (id, content, subject, type, transport_id)
SELECT 'aeccb371-417c-4f65-b513-9523498bbbe0', '
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
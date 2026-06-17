create table if not exists persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null);

INSERT INTO bay_areas(id, name) VALUES  ('NB', 'NB');
INSERT INTO bay_areas(id, name) VALUES  ('EB', 'EB');
INSERT INTO bay_areas(id, name) VALUES  ('SF', 'SF');
INSERT INTO bay_areas(id, name) VALUES  ('SB', 'SB');
INSERT INTO bay_areas(id, name) VALUES  ('SACR', 'SACR');
INSERT INTO bay_areas(id, name) VALUES  ('PENNIN', 'PENNIN');
INSERT INTO bay_areas(id, name) VALUES  ('MONTER_SANCRUZ', 'MONTER/SAN CRUZ');

INSERT INTO week_days(id, name, index_number) VALUES  ('MONDAY', 'Monday', '2');
INSERT INTO week_days(id, name, index_number) VALUES  ('TUESDAY', 'Tuesday', '3');
INSERT INTO week_days(id, name, index_number) VALUES  ('WEDNESDAY', 'Wednesday', '4');
INSERT INTO week_days(id, name, index_number) VALUES  ('THURSDAY', 'Thursday', '5');
INSERT INTO week_days(id, name, index_number) VALUES  ('FRIDAY', 'Friday', '6');
INSERT INTO week_days(id, name, index_number) VALUES  ('SATURDAY', 'Saturday', '7');
INSERT INTO week_days(id, name, index_number) VALUES  ('SUNDAY', 'Sunday', '1');

INSERT INTO academic_degrees(id, name) VALUES ('HIGH_SCHOOL_DIPLOMA', 'HS Diploma');
INSERT INTO academic_degrees(id, name) VALUES ('GED', 'GED');
INSERT INTO academic_degrees(id, name) VALUES ('VOCATIONAL_TRAINING_CERTIFICATE', 'Vocational training certificateDetails');
INSERT INTO academic_degrees(id, name) VALUES ('AA_AS', 'AA/AS');
INSERT INTO academic_degrees(id, name) VALUES ('BA', 'BA');
INSERT INTO academic_degrees(id, name) VALUES ('MASTER', 'Masters');
INSERT INTO academic_degrees(id, name) VALUES ('PHD', 'PhD');

INSERT INTO educations(id, name) VALUES ('HIGH_SCHOOL', 'High school or GED');
INSERT INTO educations(id, name) VALUES ('COLLEGE', 'College');
INSERT INTO educations(id, name) VALUES ('UNIVERSITY', 'University');

INSERT INTO languages(id, name) VALUES ('ENGLISH','English');
INSERT INTO languages(id, name) VALUES ('SPANISH','Spanish');
INSERT INTO languages(id, name) VALUES ('CHINESE','Chinese');
INSERT INTO languages(id, name) VALUES ('TAGALOG','Tagalog');
INSERT INTO languages(id, name) VALUES ('RUSSIAN','Russian');
INSERT INTO languages(id, name) VALUES ('VIETNAMESE','Vietnamese');
INSERT INTO languages(id, name) VALUES ('ARABIC','Arabic');
INSERT INTO languages(id, name) VALUES ('JAPANESE','Japanese');
INSERT INTO languages(id, name) VALUES ('FRENCH','French');
INSERT INTO languages(id, name) VALUES ('KOREAN','Korean');
INSERT INTO languages(id, name) VALUES ('HINDI','Hindi');
INSERT INTO languages(id, name) VALUES ('MANDARIN','Mandarin');
INSERT INTO languages(id, name) VALUES ('GERMAN','German');
INSERT INTO languages(id, name) VALUES ('ITALIAN','Italian');
INSERT INTO languages(id, name) VALUES ('FARSI','Farsi');

INSERT INTO categories(id, name) VALUES ('HYGIENISTS', 'Hygienists');
INSERT INTO categories(id, name) VALUES ('FRONT_OFFICE_PERSONNEL', 'Front Office Personnel');
INSERT INTO categories(id, name) VALUES ('ASSISTANTS', 'Assistants');
INSERT INTO categories(id, name) VALUES ('DENTISTS', 'Dentists/Specialists');

INSERT INTO sub_categories VALUES ('RDH','RDH','HYGIENISTS');
INSERT INTO sub_categories VALUES ('RDHAP','RDHAP','HYGIENISTS');
INSERT INTO sub_categories VALUES ('RDH_LASER','RDH with diode laser certification','HYGIENISTS');
INSERT INTO sub_categories VALUES ('PATIENT_COORDINATOR','Patient coordinator','FRONT_OFFICE_PERSONNEL');
INSERT INTO sub_categories VALUES ('REGIONAL_MANAGER','Regional manager','FRONT_OFFICE_PERSONNEL');
INSERT INTO sub_categories VALUES ('RECEPTIONIST','Receptionist','FRONT_OFFICE_PERSONNEL');
INSERT INTO sub_categories VALUES ('FINANCIAL_COORDINATOR','Financial coordinator','FRONT_OFFICE_PERSONNEL');
INSERT INTO sub_categories VALUES ('INSURANCE_COORDINATOR','Insurance coordinator','FRONT_OFFICE_PERSONNEL');
INSERT INTO sub_categories VALUES ('TREATMENT_COORDINATOR','Treatment coordinator','FRONT_OFFICE_PERSONNEL');
INSERT INTO sub_categories VALUES ('OFFICE_MANAGER','Office manager','FRONT_OFFICE_PERSONNEL');
INSERT INTO sub_categories VALUES ('ENDODONTIC_ASSISTANT','Endodontic assistant','ASSISTANTS');
INSERT INTO sub_categories VALUES ('RDA','RDA','ASSISTANTS');
INSERT INTO sub_categories VALUES ('RDAEF','RDAEF','ASSISTANTS');
INSERT INTO sub_categories VALUES ('PEDODONTIC_ASSISTANT','Pedodontic assistant','ASSISTANTS');
INSERT INTO sub_categories VALUES ('DA','DA','ASSISTANTS');
INSERT INTO sub_categories VALUES ('RDAEF1','RDAEF1','ASSISTANTS');
INSERT INTO sub_categories VALUES ('RDAEF2','RDAEF2','ASSISTANTS');
INSERT INTO sub_categories VALUES ('ORAL_SURGERY_ASSISTANT','Oral surgery assistant','ASSISTANTS');
INSERT INTO sub_categories VALUES ('ORTHODONTIC_ASSISTANT','Orthodontic assistant','ASSISTANTS');
INSERT INTO sub_categories VALUES ('PERIODONTAL_ASSISTANT','Periodontal assistant','ASSISTANTS');
INSERT INTO sub_categories VALUES ('PERIODONTIST','Periodontist','DENTISTS');
INSERT INTO sub_categories VALUES ('PROSTHODONTIST','Prosthodontist','DENTISTS');
INSERT INTO sub_categories VALUES ('ENDODONTIST','Endodontist','DENTISTS');
INSERT INTO sub_categories VALUES ('PEDODONTIST','Pedodontist','DENTISTS');
INSERT INTO sub_categories VALUES ('COSMETIC_DENTIST','Cosmetic dentist','DENTISTS');
INSERT INTO sub_categories VALUES ('ORAL_SURGEON','Oral surgeon','DENTISTS');
INSERT INTO sub_categories VALUES ('ORTHODONTIST','Orthodontist','DENTISTS');
INSERT INTO sub_categories VALUES ('DENTAL_ANESTHESIOLOGIST','Dental anesthesiologist','DENTISTS');
INSERT INTO sub_categories VALUES ('GENERAL_DENTIST','General dentist','DENTISTS');

INSERT INTO subcategory_comprised_subcategories VALUES  ('RDA','DA');
INSERT INTO subcategory_comprised_subcategories VALUES  ('RDAEF','DA');
INSERT INTO subcategory_comprised_subcategories VALUES  ('RDAEF','RDA');
INSERT INTO subcategory_comprised_subcategories VALUES  ('RDAEF1','DA');
INSERT INTO subcategory_comprised_subcategories VALUES  ('RDAEF1','RDA');
INSERT INTO subcategory_comprised_subcategories VALUES  ('RDAEF1','RDAEF');
INSERT INTO subcategory_comprised_subcategories VALUES  ('RDAEF2','DA');
INSERT INTO subcategory_comprised_subcategories VALUES  ('RDAEF2','RDA');
INSERT INTO subcategory_comprised_subcategories VALUES  ('RDAEF2','RDAEF');

INSERT INTO certificate_types(id, optional) VALUES ('DAC', TRUE);
INSERT INTO certificate_types(id, optional) VALUES ('XRAY', FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('CDA', TRUE);
INSERT INTO certificate_types(id, optional) VALUES ('CPR', FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('RDA', FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('RDAEF',FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('RDAEF1',FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('RDAEF2',FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('RDH', FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('RDHAP', FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('LIABILITY', FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('DIODE_LASER', TRUE);
INSERT INTO certificate_types(id, optional) VALUES ('DDS_OR_DMD', FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('DEA', TRUE);
INSERT INTO certificate_types(id, optional) VALUES ('NPI', FALSE);
INSERT INTO certificate_types(id, optional) VALUES ('ENDODONTIC_ASSISTANT',   TRUE);
INSERT INTO certificate_types(id, optional) VALUES ('ORAL_SURGERY_ASSISTANT', TRUE);
INSERT INTO certificate_types(id, optional) VALUES ('ORTHODONTIC_ASSISTANT',  TRUE);
INSERT INTO certificate_types(id, optional) VALUES ('PEDODONTIC_ASSISTANT',   TRUE);
INSERT INTO certificate_types(id, optional) VALUES ('PERIODONTAL_ASSISTANT',  TRUE);

INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('DA', 'DAC');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('DA', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('DA', 'XRAY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDA', 'RDA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDA', 'CDA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDA', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDAEF', 'RDA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDAEF', 'CDA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDAEF', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDAEF1', 'RDA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDAEF1', 'CDA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDAEF1', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDAEF2', 'RDA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDAEF2', 'CDA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDAEF2', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDH', 'RDH');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDH', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDH', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDH', 'DIODE_LASER');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDHAP', 'RDHAP');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDHAP', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDHAP', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDHAP', 'DIODE_LASER');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('GENERAL_DENTIST', 'DDS_OR_DMD');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('GENERAL_DENTIST', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('GENERAL_DENTIST', 'DEA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('GENERAL_DENTIST', 'NPI');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('GENERAL_DENTIST', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('COSMETIC_DENTIST', 'DDS_OR_DMD');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('COSMETIC_DENTIST', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('COSMETIC_DENTIST', 'DEA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('COSMETIC_DENTIST', 'NPI');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('COSMETIC_DENTIST', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PEDODONTIST', 'DDS_OR_DMD');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PEDODONTIST', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PEDODONTIST', 'DEA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PEDODONTIST', 'NPI');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PEDODONTIST', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PROSTHODONTIST', 'DDS_OR_DMD');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PROSTHODONTIST', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PROSTHODONTIST', 'DEA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PROSTHODONTIST', 'NPI');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PROSTHODONTIST', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PERIODONTIST', 'DDS_OR_DMD');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PERIODONTIST', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PERIODONTIST', 'DEA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PERIODONTIST', 'NPI');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PERIODONTIST', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORTHODONTIST', 'DDS_OR_DMD');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORTHODONTIST', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORTHODONTIST', 'DEA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORTHODONTIST', 'NPI');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORTHODONTIST', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ENDODONTIST', 'DDS_OR_DMD');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ENDODONTIST', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ENDODONTIST', 'DEA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ENDODONTIST', 'NPI');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ENDODONTIST', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORAL_SURGEON', 'DDS_OR_DMD');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORAL_SURGEON', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORAL_SURGEON', 'DEA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORAL_SURGEON', 'NPI');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORAL_SURGEON', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('DENTAL_ANESTHESIOLOGIST', 'DDS_OR_DMD');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('DENTAL_ANESTHESIOLOGIST', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('DENTAL_ANESTHESIOLOGIST', 'DEA');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('DENTAL_ANESTHESIOLOGIST', 'NPI');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('DENTAL_ANESTHESIOLOGIST', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDH_LASER', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDH_LASER', 'RDH');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDH_LASER', 'LIABILITY');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('RDH_LASER', 'DIODE_LASER');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ENDODONTIC_ASSISTANT', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ENDODONTIC_ASSISTANT', 'ENDODONTIC_ASSISTANT');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORAL_SURGERY_ASSISTANT', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORAL_SURGERY_ASSISTANT', 'ORAL_SURGERY_ASSISTANT');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORTHODONTIC_ASSISTANT', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('ORTHODONTIC_ASSISTANT', 'ORTHODONTIC_ASSISTANT');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PEDODONTIC_ASSISTANT', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PEDODONTIC_ASSISTANT', 'PEDODONTIC_ASSISTANT');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PERIODONTAL_ASSISTANT', 'CPR');
INSERT INTO subcategory_to_certificate_type(fk_subcategory_id, fk_certificate_type_id) VALUES ('PERIODONTAL_ASSISTANT', 'PERIODONTAL_ASSISTANT');

INSERT INTO specialities(id, name) VALUES ('GENERAL', 'General');
INSERT INTO specialities(id, name) VALUES ('CUSTOM', 'Custom');


INSERT INTO addresses(id, created_date, created_by, modified_date, updated_by, city, country, state, street, zip_code, latitude, longitude) VALUES ('182b8ea7-9b99-46bb-9085-e23972e1ef10','2018-01-15 18:13:58','ANONYMOUS','2018-01-15 18:13:58','ANONYMOUS','city','country','CZ','street','12345', 47.03736, 28.814521);

INSERT INTO contacts VALUES ('71909add-b28b-4c35-81d9-7ef3f3ce61be','2018-01-15 18:14:29','ANONYMOUS','2018-01-15 18:14:29','ANONYMOUS','iana@mdd.com','fax','firstName','last','Middle','MR','1234567890','182b8ea7-9b99-46bb-9085-e23972e1ef10',NULL);

INSERT INTO users(user_type, id, created_date, created_by, modified_date, updated_by, last_activity_date, password, status, username, notifications_enabled, fk_contact_id, comments) VALUES ('SYSTEM_USER','73af6410-5dd1-44b7-b237-2236d4452d6f','2018-01-15 18:14:29','ANONYMOUS','2018-01-15 18:14:29','ANONYMOUS','2018-01-15 18:14:29','$2a$12$xUlX9P9HNF0w7T01UfWihOvuorD6/MGVMhIrApBou/FXiTwCnihPa','ACTIVE','iana@mdd.com',true,'71909add-b28b-4c35-81d9-7ef3f3ce61be', '');

DROP TABLE qrtz_locks IF EXISTS;
DROP TABLE qrtz_scheduler_state IF EXISTS;
DROP TABLE qrtz_fired_triggers IF EXISTS;
DROP TABLE qrtz_paused_trigger_grps IF EXISTS;
DROP TABLE qrtz_calendars IF EXISTS;
DROP TABLE qrtz_blob_triggers IF EXISTS;
DROP TABLE qrtz_cron_triggers IF EXISTS;
DROP TABLE qrtz_simple_triggers IF EXISTS;
DROP TABLE qrtz_simprop_triggers IF EXISTS;
DROP TABLE qrtz_triggers IF EXISTS;
DROP TABLE qrtz_job_details IF EXISTS;

CREATE TABLE qrtz_job_details(  SCHED_NAME VARCHAR(120) NOT NULL,  JOB_NAME VARCHAR(200) NOT NULL,  JOB_GROUP VARCHAR(200) NOT NULL,  DESCRIPTION VARCHAR(250) NULL,  JOB_CLASS_NAME VARCHAR(250) NOT NULL,  IS_DURABLE BOOLEAN NOT NULL,  IS_NONCONCURRENT BOOLEAN NOT NULL,  IS_UPDATE_DATA BOOLEAN NOT NULL,  REQUESTS_RECOVERY BOOLEAN NOT NULL,  JOB_DATA BLOB NULL,  PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP));

CREATE TABLE qrtz_triggers(  SCHED_NAME VARCHAR(120) NOT NULL,  TRIGGER_NAME VARCHAR(200) NOT NULL,  TRIGGER_GROUP VARCHAR(200) NOT NULL,  JOB_NAME VARCHAR(200) NOT NULL,  JOB_GROUP VARCHAR(200) NOT NULL,  DESCRIPTION VARCHAR(250) NULL,  NEXT_FIRE_TIME NUMERIC(13) NULL,  PREV_FIRE_TIME NUMERIC(13) NULL,  PRIORITY INTEGER NULL,  TRIGGER_STATE VARCHAR(16) NOT NULL,  TRIGGER_TYPE VARCHAR(8) NOT NULL,  START_TIME NUMERIC(13) NOT NULL,  END_TIME NUMERIC(13) NULL,  CALENDAR_NAME VARCHAR(200) NULL,  MISFIRE_INSTR NUMERIC(2) NULL,  JOB_DATA BLOB NULL,  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)  REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP));

CREATE TABLE qrtz_simple_triggers(  SCHED_NAME VARCHAR(120) NOT NULL,  TRIGGER_NAME VARCHAR(200) NOT NULL,  TRIGGER_GROUP VARCHAR(200) NOT NULL,  REPEAT_COUNT NUMERIC(7) NOT NULL,  REPEAT_INTERVAL NUMERIC(12) NOT NULL,  TIMES_TRIGGERED NUMERIC(10) NOT NULL,  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP));

CREATE TABLE qrtz_cron_triggers(  SCHED_NAME VARCHAR(120) NOT NULL,  TRIGGER_NAME VARCHAR(200) NOT NULL,  TRIGGER_GROUP VARCHAR(200) NOT NULL,  CRON_EXPRESSION VARCHAR(120) NOT NULL,  TIME_ZONE_ID VARCHAR(80),  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP));

CREATE TABLE qrtz_simprop_triggers(  SCHED_NAME VARCHAR(120) NOT NULL,  TRIGGER_NAME VARCHAR(200) NOT NULL,  TRIGGER_GROUP VARCHAR(200) NOT NULL,  STR_PROP_1 VARCHAR(512) NULL,  STR_PROP_2 VARCHAR(512) NULL,  STR_PROP_3 VARCHAR(512) NULL,  INT_PROP_1 NUMERIC(9) NULL,  INT_PROP_2 NUMERIC(9) NULL,  LONG_PROP_1 NUMERIC(13) NULL,  LONG_PROP_2 NUMERIC(13) NULL,  DEC_PROP_1 NUMERIC(13,4) NULL,  DEC_PROP_2 NUMERIC(13,4) NULL,  BOOL_PROP_1 BOOLEAN NULL,  BOOL_PROP_2 BOOLEAN NULL,  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP));

CREATE TABLE qrtz_blob_triggers(  SCHED_NAME VARCHAR(120) NOT NULL,  TRIGGER_NAME VARCHAR(200) NOT NULL,  TRIGGER_GROUP VARCHAR(200) NOT NULL,  BLOB_DATA BLOB NULL,  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP));

CREATE TABLE qrtz_calendars(  SCHED_NAME VARCHAR(120) NOT NULL,  CALENDAR_NAME VARCHAR(200) NOT NULL,  CALENDAR BLOB NOT NULL,  PRIMARY KEY (SCHED_NAME,CALENDAR_NAME));

CREATE TABLE qrtz_paused_trigger_grps(  SCHED_NAME VARCHAR(120) NOT NULL,  TRIGGER_GROUP VARCHAR(200) NOT NULL,  PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP));

CREATE TABLE qrtz_fired_triggers(  SCHED_NAME VARCHAR(120) NOT NULL,  ENTRY_ID VARCHAR(95) NOT NULL,  TRIGGER_NAME VARCHAR(200) NOT NULL,  TRIGGER_GROUP VARCHAR(200) NOT NULL,  INSTANCE_NAME VARCHAR(200) NOT NULL,  FIRED_TIME NUMERIC(13) NOT NULL,  SCHED_TIME NUMERIC(13) NOT NULL,  PRIORITY INTEGER NOT NULL,  STATE VARCHAR(16) NOT NULL,  JOB_NAME VARCHAR(200) NULL,  JOB_GROUP VARCHAR(200) NULL,  IS_NONCONCURRENT BOOLEAN NULL,  REQUESTS_RECOVERY BOOLEAN NULL,  PRIMARY KEY (SCHED_NAME,ENTRY_ID));

CREATE TABLE qrtz_scheduler_state(  SCHED_NAME VARCHAR(120) NOT NULL,  INSTANCE_NAME VARCHAR(200) NOT NULL,  LAST_CHECKIN_TIME NUMERIC(13) NOT NULL,  CHECKIN_INTERVAL NUMERIC(13) NOT NULL,  PRIMARY KEY (SCHED_NAME,INSTANCE_NAME));

CREATE TABLE qrtz_locks(  SCHED_NAME VARCHAR(120) NOT NULL,  LOCK_NAME VARCHAR(40) NOT NULL,  PRIMARY KEY (SCHED_NAME,LOCK_NAME));

INSERT into system_settings(area, "group", name, encrypted, type, value) VALUES ('test', 'string', 'encrypted', TRUE, 'STRING', 'a3447917658c2d24650377ae4db578b81f0cbeae49bd606942804482426981ec'); -- value1
INSERT into system_settings(area, "group", name, encrypted, type, value) VALUES ('test', 'string', 'not_encrypted', FALSE, 'STRING', 'value2');
INSERT into system_settings(area, "group", name, encrypted, type, value) VALUES ('test', 'long', 'encrypted', TRUE, 'LONG', '0f14d0ab845dd45781dc4b11f24a6e2920cb5c7fbdd2b56e7803467f5af269dd'); -- 1
INSERT into system_settings(area, "group", name, encrypted, type, value) VALUES ('test', 'long', 'not_encrypted', FALSE, 'LONG', '2');
INSERT into system_settings(area, "group", name, encrypted, type, value) VALUES ('test', 'bool', 'encrypted', TRUE, 'BOOLEAN', '61289c51031c9aa1fe1cc506dc816e9941bf1b53fe68f9545e04e020e41f52b4'); -- true
INSERT into system_settings(area, "group", name, encrypted, type, value) VALUES ('test', 'bool', 'not_encrypted', FALSE, 'BOOLEAN', 'false');
INSERT into system_settings(area, "group", name, encrypted, type, value) VALUES ('test', 'date', 'encrypted', TRUE, 'DATE', 'd4f09f3c2370f19ec0b47b7777af900f07768231827dde088327885419d49368'); -- 2007-03-12
INSERT into system_settings(area, "group", name, encrypted, type, value) VALUES ('test', 'date', 'not_encrypted', FALSE, 'DATE', '2007-12-03');

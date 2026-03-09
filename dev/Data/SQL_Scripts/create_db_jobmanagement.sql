-- Title :             SQL script to create the DB of the jobmngt project
-- Version :           0.2
-- Creation date :     24/08/2023
-- Last modification date : 18/02/2026
-- Author :            Grégory SMITS
-- Description :       Script used to create the DB of the job management app.
--                     Tested on PostgreSQL 10

-- +----------------------------------------------------------------------------------------------+
-- | Suppress tables if they exist                                                                |
-- +----------------------------------------------------------------------------------------------+

drop table if exists appuser;
drop table if exists sector;
drop table if exists qualificationlevel;

-- +----------------------------------------------------------------------------------------------+
-- | Tables creation                                                                              |
-- +----------------------------------------------------------------------------------------------+

create table sector
(
  id       serial primary key,
  label varchar(50) not null unique
);

create table qualificationlevel
(
  id       serial primary key,
  label varchar(50) not null unique
);

create table appuser
(
  mail             varchar(100) primary key,
  password         varchar(100) not null,
  usertype            varchar(20) not null
);
alter table appuser add constraint appuser_type_check check (usertype in ('applicant', 'company', 'admin'));
alter table appuser add constraint appuser_mail_check check (mail ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');
alter table appuser add constraint appuser_password_check check (length(password) >= 3);

-- +----------------------------------------------------------------------------------------------+
-- | Insert companies                                                                             |
-- +----------------------------------------------------------------------------------------------+

insert into appuser(mail, password, usertype) values ('admin@imt-atlantique.fr', 'password123', 'admin');
insert into appuser(mail, password, usertype) values ('toto@mytoto.fr', 'password456', 'applicant');
insert into appuser(mail, password, usertype) values ('contact@sportinnovation.fr', 'password789', 'company');


-- +----------------------------------------------------------------------------------------------+
-- | Insert sectors and qualification levels                                                      |
-- +----------------------------------------------------------------------------------------------+

-- Some sectors

insert into sector(id, label) values (nextval('sector_id_seq'), 'Purchase/Logistic');                  --  1
insert into sector(id, label) values (nextval('sector_id_seq'), 'Administration');             --  2
insert into sector(id, label) values (nextval('sector_id_seq'), 'Agriculture');                        --  3
insert into sector(id, label) values (nextval('sector_id_seq'), 'Agrofood');                    --  4
insert into sector(id, label) values (nextval('sector_id_seq'), 'Insurance');                          --  5
insert into sector(id, label) values (nextval('sector_id_seq'), 'Audit/Advise/Expertise');           --  6
insert into sector(id, label) values (nextval('sector_id_seq'), 'Public works/Real estate');                     --  7
insert into sector(id, label) values (nextval('sector_id_seq'), 'Trade');                         --  8
insert into sector(id, label) values (nextval('sector_id_seq'), 'Communication/Art/Media/Fashion');       --  9
insert into sector(id, label) values (nextval('sector_id_seq'), 'Accounting');                       -- 10
insert into sector(id, label) values (nextval('sector_id_seq'), 'Direction/Execution');       -- 11
insert into sector(id, label) values (nextval('sector_id_seq'), 'Distribution/Sale');              -- 12
insert into sector(id, label) values (nextval('sector_id_seq'), 'Electronic/Microelectronic');     -- 13
insert into sector(id, label) values (nextval('sector_id_seq'), 'Environment');                      -- 14
insert into sector(id, label) values (nextval('sector_id_seq'), 'Finance/Bank');                     -- 15
insert into sector(id, label) values (nextval('sector_id_seq'), 'Training/Teaching');             -- 16
insert into sector(id, label) values (nextval('sector_id_seq'), 'Hotel/Restaurant/Tourism');   -- 17
insert into sector(id, label) values (nextval('sector_id_seq'), 'Industry/Engineering/Production');    -- 18
insert into sector(id, label) values (nextval('sector_id_seq'), 'Computer science');                       -- 19
insert into sector(id, label) values (nextval('sector_id_seq'), 'Juridique/Fiscal/Droit');             -- 20
insert into sector(id, label) values (nextval('sector_id_seq'), 'Marketing');                          -- 21
insert into sector(id, label) values (nextval('sector_id_seq'), 'Public/Parapublic');                  -- 22
insert into sector(id, label) values (nextval('sector_id_seq'), 'Human resources');                -- 23
insert into sector(id, label) values (nextval('sector_id_seq'), 'Health/Social/Biology/HHumanitarian');  -- 24
insert into sector(id, label) values (nextval('sector_id_seq'), 'Telecom/Networking');                    -- 25

-- Some qualification levels

insert into qualificationlevel(id, label) values (nextval('qualificationlevel_id_seq'), 'Professional level');   --  1
insert into qualificationlevel(id, label) values (nextval('qualificationlevel_id_seq'), 'A-diploma');       --  2
insert into qualificationlevel(id, label) values (nextval('qualificationlevel_id_seq'), 'Licence');     --  3
insert into qualificationlevel(id, label) values (nextval('qualificationlevel_id_seq'), 'Master');     --  4
insert into qualificationlevel(id, label) values (nextval('qualificationlevel_id_seq'), 'PhD');  --  5
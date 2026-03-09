-- Title :            SQL script to create the DB of the jobmngt project
-- Version :          1.0
-- Creation date :    09/03/2026
-- Last modification date : 09/03/2026
-- Authors :           Yohan AZIKA EROS & Djelloul DJELLAL
-- Description :      Script used to create the DB of the job management app.
--                    Tested on PostgreSQL 15

-- +----------------------------------------------------------------------------------------------+
-- | Suppress tables if they exist                                                                |
-- +----------------------------------------------------------------------------------------------+

drop table if exists message_to_application cascade;
drop table if exists message_to_offer cascade;
drop table if exists application_sector cascade;
drop table if exists joboffer_sector cascade;
drop table if exists applications cascade;
drop table if exists joboffers cascade;
drop table if exists candidates cascade;
drop table if exists companies cascade;
drop table if exists admins cascade;
drop table if exists appusers cascade;
drop table if exists qualificationlevels cascade;
drop table if exists sectors cascade;

-- +----------------------------------------------------------------------------------------------+
-- | Tables creation                                                                              |
-- +----------------------------------------------------------------------------------------------+

create table sectors (
                         id       serial primary key,
                         label    varchar(50) not null unique
);

create table qualificationlevels (
                                     id       serial primary key,
                                     label    varchar(50) not null unique,
                                     rank     smallint unique  -- compare qualification levels (e.g., minimum required)
);

create table appusers (
                          id       serial primary key,
                          mail     varchar(100) not null unique,
                          password varchar(255) not null,
                          usertype varchar(20)  not null,

                          constraint ck_password_length check (length(password) >= 3),
                          constraint ck_usertype check (usertype in ('admin','company','applicant')),
                          constraint ck_mail_format check (mail ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
    );

create table admins (
                        id integer primary key,
                        constraint fk_admin_user foreign key (id)
                            references appusers(id) on delete cascade
);

create table companies (
                           id           integer primary key,
                           denomination varchar(100) not null,
                           description  text,
                           city         varchar(100),

                           constraint fk_company_user foreign key (id)
                               references appusers(id) on delete cascade
);

create table candidates (
                            id        integer primary key,
                            lastname  varchar(50) not null,
                            firstname varchar(50),
                            city      varchar(100),

                            constraint fk_candidate_user foreign key (id)
                                references appusers(id) on delete cascade
);

create table joboffers (
                           id                    serial primary key,
                           title                 varchar(120) not null,
                           taskdescription       text not null,
                           publicationdate       date not null default current_date,
                           company_id            integer not null,
                           qualificationlevel_id integer not null,

                           constraint fk_joboffer_company foreign key (company_id)
                               references companies(id) on delete cascade,
                           constraint fk_joboffer_qual foreign key (qualificationlevel_id)
                               references qualificationlevels(id)
);

create table applications (
                              id                    serial primary key,
                              cv                    text not null,
                              appdate               date not null default current_date,
                              candidate_id          integer not null,
                              qualificationlevel_id integer not null,

                              constraint fk_application_candidate foreign key (candidate_id)
                                  references candidates(id) on delete cascade,
                              constraint fk_application_qual foreign key (qualificationlevel_id)
                                  references qualificationlevels(id)
);

create table joboffer_sector (
                                 joboffer_id integer not null,
                                 sector_id   integer not null,
                                 primary key (joboffer_id, sector_id),

                                 constraint fk_joboffer_sector_offer foreign key (joboffer_id)
                                     references joboffers(id) on delete cascade,
                                 constraint fk_joboffer_sector_sector foreign key (sector_id)
                                     references sectors(id)
);

create table application_sector (
                                    application_id integer not null,
                                    sector_id      integer not null,
                                    primary key (application_id, sector_id),

                                    constraint fk_application_sector_app foreign key (application_id)
                                        references applications(id) on delete cascade,
                                    constraint fk_application_sector_sector foreign key (sector_id)
                                        references sectors(id)
);

create table message_to_offer (
                                  id              serial primary key,
                                  publicationdate date not null default current_date,
                                  message         text not null,
                                  joboffer_id     integer not null,
                                  application_id  integer not null,

                                  constraint fk_mto_offer foreign key (joboffer_id)
                                      references joboffers(id) on delete cascade,
                                  constraint fk_mto_application foreign key (application_id)
                                      references applications(id) on delete cascade,

                                  constraint uq_mto_pair unique (joboffer_id, application_id)
);

create table message_to_application (
                                        id              serial primary key,
                                        publicationdate date not null default current_date,
                                        message         text not null,
                                        application_id  integer not null,
                                        joboffer_id     integer not null,

                                        constraint fk_mta_application foreign key (application_id)
                                            references applications(id) on delete cascade,
                                        constraint fk_mta_offer foreign key (joboffer_id)
                                            references joboffers(id) on delete cascade,

                                        constraint uq_mta_pair unique (application_id, joboffer_id)
);

-- +----------------------------------------------------------------------------------------------+
-- | Insert initial users                                                                         |
-- +----------------------------------------------------------------------------------------------+

-- Admin
insert into appusers(mail, password, usertype) values ('admin@imt-atlantique.fr', 'password123', 'admin');
insert into admins(id) values (lastval());

-- Applicant
insert into appusers(mail, password, usertype) values ('toto@mytoto.fr', 'password456', 'applicant');
insert into candidates(id, lastname, firstname, city) values (lastval(), 'Toto', 'MyToto', 'Brest');

-- Company
insert into appusers(mail, password, usertype) values ('contact@sportinnovation.fr', 'password789', 'company');
insert into companies(id, denomination, description, city) values (lastval(), 'Sport Innovation', 'Equipement sportif', 'Paris');


-- +----------------------------------------------------------------------------------------------+
-- | Insert sectors and qualification levels                                                      |
-- +----------------------------------------------------------------------------------------------+

-- Some sectors

insert into sectors(label) values ('Purchase/Logistic');
insert into sectors(label) values ('Administration');
insert into sectors(label) values ('Agriculture');
insert into sectors(label) values ('Agrofood');
insert into sectors(label) values ('Insurance');
insert into sectors(label) values ('Audit/Advise/Expertise');
insert into sectors(label) values ('Public works/Real estate');
insert into sectors(label) values ('Trade');
insert into sectors(label) values ('Communication/Art/Media/Fashion');
insert into sectors(label) values ('Accounting');
insert into sectors(label) values ('Direction/Execution');
insert into sectors(label) values ('Distribution/Sale');
insert into sectors(label) values ('Electronic/Microelectronic');
insert into sectors(label) values ('Environment');
insert into sectors(label) values ('Finance/Bank');
insert into sectors(label) values ('Training/Teaching');
insert into sectors(label) values ('Hotel/Restaurant/Tourism');
insert into sectors(label) values ('Industry/Engineering/Production');
insert into sectors(label) values ('Computer science');
insert into sectors(label) values ('Juridique/Fiscal/Droit');
insert into sectors(label) values ('Marketing');
insert into sectors(label) values ('Public/Parapublic');
insert into sectors(label) values ('Human resources');
insert into sectors(label) values ('Health/Social/Biology/Humanitarian');
insert into sectors(label) values ('Telecom/Networking');

-- Some qualification levels

insert into qualificationlevels(label, rank) values ('Professional level', 1);
insert into qualificationlevels(label, rank) values ('A-diploma', 2);
insert into qualificationlevels(label, rank) values ('Licence', 3);
insert into qualificationlevels(label, rank) values ('Master', 4);
insert into qualificationlevels(label, rank) values ('PhD', 5);

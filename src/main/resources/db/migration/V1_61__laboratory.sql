create table incubator (
  id bigserial,
  player_id bigint not null,
  type varchar(20) not null,
  start_timestamp timestamp not null,
  finish_timestamp timestamp not null,
  resources text not null,
  primary key (id)
);

alter table building drop column if exists upgrade_started;
alter table building drop column if exists upgrade_finished;
alter table building drop column if exists wip_started;
alter table building drop column if exists wip_finished;
alter table building drop column if exists wip_ref;

alter table progress add column if not exists incubators int not null default 1;
alter table progress add column if not exists lab_speed int not null default 100;

insert into dynamic_property (category, type, level, value1) values
('BUILDING', 'LABORATORY_INCUBATORS', 1, 1),
('BUILDING', 'LABORATORY_INCUBATORS', 3, 1),
('BUILDING', 'LABORATORY_INCUBATORS', 5, 1),
('BUILDING', 'LABORATORY_INCUBATORS', 7, 1),
('BUILDING', 'LABORATORY_INCUBATORS', 9, 1),

('BUILDING', 'LABORATORY_SPEED', 2, 20),
('BUILDING', 'LABORATORY_SPEED', 4, 20),
('BUILDING', 'LABORATORY_SPEED', 6, 20),
('BUILDING', 'LABORATORY_SPEED', 8, 20),
('BUILDING', 'LABORATORY_SPEED', 10, 20),

('UPGRADE_TIME', 'SIMPLE_GENOME_TIME', 1, 0),
('UPGRADE_TIME', 'COMMON_GENOME_TIME', 1, 0),
('UPGRADE_TIME', 'UNCOMMON_GENOME_TIME', 1, 60),
('UPGRADE_TIME', 'RARE_GENOME_TIME', 1, 3600),
('UPGRADE_TIME', 'EPIC_GENOME_TIME', 1, 14400);

insert into dynamic_property (category, type, level, resource_type, value1) values
('UPGRADE_COST', 'SIMPLE_GENOME_COST', 1, 'SIMPLE_GENOME', 60),
('UPGRADE_COST', 'COMMON_GENOME_COST', 1, 'COMMON_GENOME', 60),
('UPGRADE_COST', 'UNCOMMON_GENOME_COST', 1, 'UNCOMMON_GENOME', 60),
('UPGRADE_COST', 'RARE_GENOME_COST', 1, 'RARE_GENOME', 60),
('UPGRADE_COST', 'EPIC_GENOME_COST', 1, 'EPIC_GENOME', 60);
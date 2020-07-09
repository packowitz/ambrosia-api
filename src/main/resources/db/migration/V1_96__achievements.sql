alter table progress add column if not exists expedition_speed int not null default 100;

drop table if exists achievements;
create table achievements (
    player_id bigint primary key,
    simple_incubations_done int not null default 0,
    common_incubations_done int not null default 0,
    uncommon_incubations_done int not null default 0,
    rare_incubations_done int not null default 0,
    epic_incubations_done int not null default 0,
    expeditions_done int not null default 0,
    odd_jobs_done int not null default 0,
    daily_rewards_claimed int not null default 0,
    academy_xp_gained bigint not null default 0,
    academy_asc_gained bigint not null default 0
);

insert into achievements (player_id) (select id from player);

delete from dynamic_property where type = 'EXPEDITION_PROGRESS';
insert into dynamic_property (category, type, level, value1, version) values
('PLAYER', 'EXPEDITION_PROGRESS', 1, 100, 1),
('PLAYER', 'EXPEDITION_PROGRESS', 2, 250, 1),
('PLAYER', 'EXPEDITION_PROGRESS', 3, 500, 1),
('PLAYER', 'EXPEDITION_PROGRESS', 4, 1000, 1),
('PLAYER', 'EXPEDITION_PROGRESS', 5, 2500, 1);

delete from property_version where property_type = 'EXPEDITION_PROGRESS';
insert into property_version (property_type, version, active) values ('EXPEDITION_PROGRESS', 1, true);
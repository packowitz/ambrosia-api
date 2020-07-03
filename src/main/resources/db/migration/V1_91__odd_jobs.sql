alter table progress add column if not exists number_odd_jobs integer not null default 3;

create table if not exists odd_job_base (
    id bigserial not null,
    active boolean not null,
    level int not null,
    rarity varchar(20) not null,
    job_type varchar(32) not null,
    job_amount int not null,
    loot_box_id bigint not null,
    primary key (id)
);

create table if not exists odd_job (
    id bigserial not null,
    player_id bigint not null,
    odd_job_base_id bigint not null,
    created timestamp not null,
    level int not null,
    rarity varchar(20) not null,
    job_type varchar(32) not null,
    job_amount int not null,
    job_amount_done int not null,
    loot_box_id bigint not null,
    primary key (id)
);
create index if not exists odd_job_player_id_index on odd_job (player_id);

insert into dynamic_property(category, type, level, resource_type, value1) values
('PLAYER', 'DAILY_REWARD', 1, 'PREMIUM_STEAM', 15),
('PLAYER', 'DAILY_REWARD', 2, 'PREMIUM_COGWHEELS', 5),
('PLAYER', 'DAILY_REWARD', 3, 'SIMPLE_GENOME', 180),
('PLAYER', 'DAILY_REWARD', 4, 'IRON', 50),
('PLAYER', 'DAILY_REWARD', 5, 'UNCOMMON_GENOME', 60),
('PLAYER', 'DAILY_REWARD', 6, 'BROWN_COAL', 50),
('PLAYER', 'DAILY_REWARD', 7, 'COMMON_GENOME', 90),
('PLAYER', 'DAILY_REWARD', 8, 'RUBIES', 50),
('PLAYER', 'DAILY_REWARD', 9, 'STEEL', 50),
('PLAYER', 'DAILY_REWARD', 10, 'RARE_GENOME', 15);

drop table if exists daily_activity;
create table daily_activity (
    player_id bigint primary key,
    day1 timestamp,
    day1claimed boolean not null default false,
    day2 timestamp,
    day2claimed boolean not null default false,
    day3 timestamp,
    day3claimed boolean not null default false,
    day4 timestamp,
    day4claimed boolean not null default false,
    day5 timestamp,
    day5claimed boolean not null default false,
    day6 timestamp,
    day6claimed boolean not null default false,
    day7 timestamp,
    day7claimed boolean not null default false,
    day8 timestamp,
    day8claimed boolean not null default false,
    day9 timestamp,
    day9claimed boolean not null default false,
    day10 timestamp,
    day10claimed boolean not null default false
);
insert into daily_activity (player_id) (select id from player);

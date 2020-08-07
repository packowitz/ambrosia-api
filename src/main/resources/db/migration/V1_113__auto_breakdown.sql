alter table progress add column if not exists auto_break_down_enabled boolean not null default false;

drop table if exists auto_breakdown_configuration;
create table auto_breakdown_configuration (
    player_id bigint primary key,
    simple_min_jewel_slots integer not null default 0,
    simple_min_quality integer not null default 0,
    common_min_jewel_slots integer not null default 0,
    common_min_quality integer not null default 0,
    uncommon_min_jewel_slots integer not null default 0,
    uncommon_min_quality integer not null default 0,
    rare_min_jewel_slots integer not null default 0,
    rare_min_quality integer not null default 0,
    epic_min_jewel_slots integer not null default 0,
    epic_min_quality integer not null default 0,
    legendary_min_jewel_slots integer not null default 0,
    legendary_min_quality integer not null default 0
);

insert into auto_breakdown_configuration (player_id) (select id from player);

alter table achievements add column if not exists gear_breakdown bigint not null default 0;
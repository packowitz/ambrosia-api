create table fight_stage_config (
    id bigserial,
    name varchar(30) not null,
    default_config boolean not null default false,
    debuffs_removed boolean not null default false,
    debuff_duration_change int not null default 0,
    buffs_removed boolean not null default false,
    buff_duration_change int not null default 0,
    hp_healing int not null default 0,
    armor_repair int not null default 0,
    speed_bar_change varchar not null default 'NONE',
    primary key (id)
);

alter table fight add column stage_config_id bigint;

insert into fight_stage_config VALUES (1, 'Default', true, true, 0, false, 0, 30, 10, 'NONE');

update fight set stage_config_id = 1;

alter table fight alter column stage_config_id set not null;

alter table battle_hero add hero_initiative int;


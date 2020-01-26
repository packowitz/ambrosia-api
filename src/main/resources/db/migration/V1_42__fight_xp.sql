alter table fight add if not exists xp int not null default 500;
alter table fight add if not exists level int not null default 5;
alter table fight add if not exists asc_points int not null default 10;

-- clear existing battles
delete from battle_step_hero_state_buff where 1=1;
delete from battle_step_hero_state where 1=1;
delete from battle_step_action where 1=1;
delete from battle_step where 1=1;
delete from battle_hero_buff where 1=1;
delete from battle_hero where 1=1;
delete from battle where 1=1;

alter table battle_hero add if not exists hero_id bigint not null;

alter table hero_base add if not exists max_asc_level int not null default 10;
alter table hero add if not exists skill_points int not null default 0;
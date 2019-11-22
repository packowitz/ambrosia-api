-- clear existing battles
delete from ambrosia.public.battle_step_hero_state_buff where 1=1;
delete from ambrosia.public.battle_step_hero_state where 1=1;
delete from ambrosia.public.battle_step_action where 1=1;
delete from ambrosia.public.battle_step where 1=1;
delete from ambrosia.public.battle_hero_buff where 1=1;
delete from ambrosia.public.battle_hero where 1=1;
delete from ambrosia.public.battle where 1=1;

alter table battle add player_name varchar(36) not null;
alter table battle add opponent_name varchar(36) not null;

alter table battle_step add acting_hero_name varchar(50) not null;
alter table battle_step add target_name varchar(50) not null;
alter table battle_step add used_skill_name varchar(30) not null;

alter table battle_step_action add hero_name varchar(50) not null;

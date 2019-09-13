alter table hero alter column skill2 drop not null;
alter table hero alter column skill2 drop default;
alter table hero alter column skill3 drop not null;
alter table hero alter column skill3 drop default;
alter table hero alter column skill4 drop not null;
alter table hero alter column skill4 drop default;
alter table hero alter column skill5 drop not null;
alter table hero alter column skill5 drop default;
alter table hero alter column skill6 drop not null;
alter table hero alter column skill6 drop default;
alter table hero alter column skill7 drop not null;
alter table hero alter column skill7 drop default;

create table battle (
  id bigserial,
  type varchar(25) not null,
  status varchar(25) not null,
  player_id bigint not null,
  opponent_id bigint not null,
  started timestamp not null,
  last_action timestamp not null,
  active_hero varchar(25) not null,
  turns_done int not null,
  hero1id bigint,
  hero2id bigint,
  hero3id bigint,
  hero4id bigint,
  opp_hero1id bigint,
  opp_hero2id bigint,
  opp_hero3id bigint,
  opp_hero4id bigint,
  primary key (id)
);

create table battle_hero (
  id bigserial,
  player_id bigint not null,
  status varchar(25) not null,
  hero_base_id bigint not null,
  color varchar(10) not null,
  level int not null,
  stars int not null,
  asc_lvl int not null,
  position varchar(25) not null,
  skill1lvl int not null,
  skill2lvl int,
  skill2cooldown int,
  skill3lvl int,
  skill3cooldown int,
  skill4lvl int,
  skill4cooldown int,
  skill5lvl int,
  skill5cooldown int,
  skill6lvl int,
  skill6cooldown int,
  skill7lvl int,
  skill7cooldown int,
  hero_strength int not null,
  hero_hp int not null,
  hero_armor int not null,
  hero_crit int not null,
  hero_crit_mult int not null,
  hero_dexterity int not null,
  hero_resistance int not null,
  current_hp int not null,
  current_armor int not null,
  current_speed_bar int not null,
  hero_lifesteal int not null,
  hero_counter_chance int not null,
  hero_reflect int not null,
  hero_dodge_chance int not null,
  hero_speed int not null,
  hero_armor_piercing int not null,
  hero_armor_extra_dmg int not null,
  hero_health_extra_dmg int not null,
  hero_red_damage_inc int not null,
  hero_green_damage_inc int not null,
  hero_blue_damage_inc int not null,
  hero_healing_inc int not null,
  hero_super_crit_chance int not null,
  hero_buff_intensity_inc int not null,
  hero_debuff_intensity_inc int not null,
  primary key (id)
);

create table battle_hero_buff (
  id bigserial,
  battle_hero_id bigint,
  buff varchar(25) not null,
  intensity int not null,
  duration int not null,
  source_hero_id bigint not null,
  primary key (id)
);

create table battle_step (
  id bigserial,
  battle_id bigint,
  turn int not null,
  acting_hero varchar(25) not null,
  used_skill int not null,
  target varchar(25) not null,
  primary key (id)
);

create table battle_step_action (
  id bigserial,
  battle_step_id bigint,
  hero_position varchar(25) not null,
  crit boolean,
  super_crit boolean,
  armor_diff int,
  health_diff int,
  buff varchar(25),
  buff_intensity int,
  buff_duration int,
  buff_duration_change int,
  primary key (id)
);

ALTER TABLE hero_skill_action ALTER COLUMN effect_value TYPE int;


insert into dynamic_property values (default, 'BUFF', 'STRENGTH_BUFF', 1, 'STRENGTH_PERC', 5);
insert into dynamic_property values (default, 'BUFF', 'STRENGTH_BUFF', 2, 'STRENGTH_PERC', 10);
insert into dynamic_property values (default, 'BUFF', 'STRENGTH_BUFF', 3, 'STRENGTH_PERC', 18);
insert into dynamic_property values (default, 'BUFF', 'STRENGTH_BUFF', 4, 'STRENGTH_PERC', 30);
insert into dynamic_property values (default, 'BUFF', 'STRENGTH_BUFF', 5, 'STRENGTH_PERC', 45);
insert into dynamic_property values (default, 'BUFF', 'ARMOR_BUFF', 1, 'ARMOR_PERC', 5);
insert into dynamic_property values (default, 'BUFF', 'ARMOR_BUFF', 2, 'ARMOR_PERC', 10);
insert into dynamic_property values (default, 'BUFF', 'ARMOR_BUFF', 3, 'ARMOR_PERC', 18);
insert into dynamic_property values (default, 'BUFF', 'ARMOR_BUFF', 4, 'ARMOR_PERC', 30);
insert into dynamic_property values (default, 'BUFF', 'ARMOR_BUFF', 5, 'ARMOR_PERC', 45);

insert into dynamic_property values (default, 'BATTLE', 'BATTLE_ARMOR', 30, null, 1, 0);
insert into dynamic_property values (default, 'BATTLE', 'BATTLE_ARMOR', 75, null, 2, 5);
insert into dynamic_property values (default, 'BATTLE', 'BATTLE_ARMOR', 150, null, 3, 20);
insert into dynamic_property values (default, 'BATTLE', 'BATTLE_ARMOR', 300, null, 4, 30);
insert into dynamic_property values (default, 'BATTLE', 'BATTLE_ARMOR', 600, null, 5, 40);
insert into dynamic_property values (default, 'BATTLE', 'BATTLE_ARMOR', 1000, null, 7, 50);
insert into dynamic_property values (default, 'BATTLE', 'BATTLE_ARMOR', 10000, null, 10, 70);

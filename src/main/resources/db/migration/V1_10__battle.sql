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
  status varchar(25) not null,
  hero_base_id bigint not null,
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
  strength_bonus int not null,
  hero_hp int not null,
  hero_armor int not null,
  armor_bonus int not null,
  hero_initiative int not null,
  hero_crit int not null,
  crit_bonus int not null,
  hero_crit_mult int not null,
  crit_mult_bonus int not null,
  hero_dexterity int not null,
  dexterity_bonus int not null,
  hero_resistance int not null,
  resistance_bonus int not null,
  current_hp int not null,
  current_armor int not null,
  current_speed_bar int not null,
  hero_lifesteal int not null,
  lifesteal_bonus int not null,
  hero_counter_chance int not null,
  counter_chance_bonus int not null,
  hero_reflect int not null,
  reflect_bonus int not null,
  hero_dodge_chance int not null,
  dodge_chance_bonus int not null,
  hero_speed int not null,
  speed_bonus int not null,
  hero_armor_piercing int not null,
  armor_piercing_bonus int not null,
  hero_armor_extra_dmg int not null,
  armor_extra_dmg_bonus int not null,
  hero_health_extra_dmg int not null,
  health_extra_dmg_bonus int not null,
  hero_red_damage_inc int not null,
  red_damage_inc_bonus int not null,
  hero_green_damage_inc int not null,
  green_damage_inc_bonus int not null,
  hero_blue_damage_inc int not null,
  blue_damage_inc_bonus int not null,
  hero_healing_inc int not null,
  healing_inc_bonus int not null,
  hero_super_crit_chance int not null,
  super_crit_chance_bonus int not null,
  hero_buff_intensity_inc int not null,
  buff_intensity_inc_bonus int not null,
  hero_debuff_intensity_inc int not null,
  debuff_intensity_inc_bonus int not null,
  primary key (id)
);

create table battle_hero_buff (
  id bigserial,
  battle_hero_id bigint not null,
  buff varchar(25) not null,
  intensity int not null,
  duration int not null,
  source_hero_id bigint not null,
  primary key (id)
);

create table battle_step (
  id bigserial,
  battle_id bigint not null,
  turn int not null,
  acting_hero varchar(25) not null,
  used_skill int not null,
  target varchar(25) not null,
  primary key (id)
);

create table battle_step_action (
  id bigserial,
  battle_step_id bigint not null,
  hero_position varchar(25) not null,
  crit boolean,
  super_crit boolean,
  armor_diff int,
  healthDiff int,
  buff varchar(25),
  buff_intensity int,
  buff_duration int,
  buff_duration_change int,
  primary key (id)
);
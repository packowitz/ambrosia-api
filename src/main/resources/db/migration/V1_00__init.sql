create table hero_base (
  id bigserial,
  name varchar(50) not null,
  rarity varchar(20) not null,
  hero_class varchar(25) not null,
  color varchar(10) not null,
  hero_type varchar(25) not null,
  strength_base int not null,
  strength_full int not null,
  hp_base int not null,
  hp_full int not null,
  armor_base int not null,
  armor_full int not null,
  speed int not null,
  speed_asc int not null,
  crit int not null,
  crit_asc int not null,
  crit_mult int not null,
  crit_mult_asc int not null,
  dexterity int not null,
  dexterity_asc int not null,
  resistance int not null,
  resistance_asc int not null,
  primary key (id)
);

create table hero_skill (
  id bigserial,
  hero_id bigint,
  number int not null,
  name varchar(30) not null,
  passive boolean not null,
  skill_active_trigger varchar(20) not null,
  init_cooldown int not null,
  cooldown int not null,
  target varchar(255) not null,
  description varchar(255) not null,
  max_level int not null,
  primary key (id)
);

create table hero_skill_action (
  id bigserial,
  skill_id bigint,
  position int not null,
  trigger varchar(25) not null,
  trigger_value varchar(25) not null,
  target varchar(25) not null,
  effect varchar(25) not null,
  effect_value numeric(10, 2) not null,
  primary key (id)
);

create table hero_skill_level (
  id bigserial,
  skill_id bigint,
  level int not null,
  description varchar(50) not null,
  primary key (id)
);

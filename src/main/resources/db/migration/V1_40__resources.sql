create table resources (
  player_id bigint,
  steam int not null,
  steam_max int not null,
  steam_produce_seconds int not null,
  steam_last_production timestamp with time zone not null,
  cogwheels int not null,
  cogwheels_max int not null,
  cogwheels_produce_seconds int not null,
  cogwheels_last_production timestamp with time zone not null,
  tokens int not null,
  tokens_max int not null,
  tokens_produce_seconds int not null,
  tokens_last_production timestamp with time zone not null,
  premium_steam int not null default 0,
  premium_steam_max int not null,
  premium_cogwheels int not null default 0,
  premium_cogwheels_max int not null,
  premium_tokens int not null default 0,
  premium_tokens_max int not null,
  rubies int not null default 0,
  coins int not null,
  metal int not null default 0,
  metal_max int not null,
  iron int not null default 0,
  iron_max int not null,
  steal int not null default 0,
  steal_max int not null,
  wood int not null default 0,
  wood_max int not null,
  brown_coal int not null default 0,
  brown_coal_max int not null,
  black_coal int not null default 0,
  black_coal_max int not null,
  simple_genome int not null default 0,
  common_genome int not null default 0,
  uncommon_genome int not null default 0,
  rare_genome int not null default 0,
  epic_genome int not null default 0,
  primary key (player_id)
);

insert into resources(player_id, steam, steam_max, steam_last_production, steam_produce_seconds, premium_steam_max,
                      cogwheels, cogwheels_max, cogwheels_last_production, cogwheels_produce_seconds, premium_cogwheels_max,
                      tokens, tokens_max, tokens_last_production, tokens_produce_seconds, premium_tokens_max,
                      metal_max, iron_max, steal_max, wood_max, brown_coal_max, black_coal_max, coins)
                      (
                        select id, 30, 30, now(), 300, 20,
                               0, 5, now(), 3600, 10,
                               0, 4, now(), 7200, 5,
                               15, 12, 10, 15, 12, 10, 20
                        from player
                      );

alter table dynamic_property add if not exists resource_type varchar(30);

insert into dynamic_property (category, type, level, resource_type, value1) values
  ('RESOURCES', 'PLAYER_LVL_RESOURCES', '1', 'STEAM', 30),
  ('RESOURCES', 'PLAYER_LVL_RESOURCES', '1', 'STEAM_MAX', 30),
  ('RESOURCES', 'PLAYER_LVL_RESOURCES', '1', 'COGWHEELS', 0),
  ('RESOURCES', 'PLAYER_LVL_RESOURCES', '1', 'COGWHEELS_MAX', 5),
  ('RESOURCES', 'PLAYER_LVL_RESOURCES', '1', 'TOKENS', 0),
  ('RESOURCES', 'PLAYER_LVL_RESOURCES', '1', 'TOKENS_MAX', 4),
  ('RESOURCES', 'PLAYER_LVL_RESOURCES', '1', 'COINS', 20),
  ('RESOURCES', 'STORAGE_RESOURCES', '1', 'PREMIUM_STEAM_MAX', 20),
  ('RESOURCES', 'STORAGE_RESOURCES', '1', 'PREMIUM_COGWHEELS_MAX', 10),
  ('RESOURCES', 'STORAGE_RESOURCES', '1', 'PREMIUM_TOKENS_MAX', 5),
  ('RESOURCES', 'STORAGE_RESOURCES', '1', 'METAL_MAX', 15),
  ('RESOURCES', 'STORAGE_RESOURCES', '1', 'IRON_MAX', 12),
  ('RESOURCES', 'STORAGE_RESOURCES', '1', 'STEAL_MAX', 10),
  ('RESOURCES', 'STORAGE_RESOURCES', '1', 'WOOD_MAX', 15),
  ('RESOURCES', 'STORAGE_RESOURCES', '1', 'BROWN_COAL_MAX', 12),
  ('RESOURCES', 'STORAGE_RESOURCES', '1', 'BLACK_COAL_MAX', 10);

alter table map add if not exists discovery_steam_cost int not null default 5;
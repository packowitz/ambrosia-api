drop table if exists achievement_reward;
create table achievement_reward (
    id bigserial primary key,
    starter boolean not null,
    name varchar(64),
    achievement_type varchar(32) not null,
    achievement_amount bigint not null,
    follow_up_reward bigint,
    loot_box_id bigint not null
);

drop table if exists player_achievement_reward;
create table player_achievement_reward (
    id bigserial primary key,
    player_id bigint not null,
    reward_id bigint not null,
    claimed boolean not null default false
);
create index if not exists player_achievement_reward_player_id_index on player_achievement_reward (player_id);

update progress set expedition_level = 1 where expedition_level > 1;

alter table achievements add column if not exists steam_used bigint not null default 0;
alter table achievements add column if not exists cogwheels_used bigint not null default 0;
alter table achievements add column if not exists tokens_used bigint not null default 0;
alter table achievements add column if not exists coins_used bigint not null default 0;
alter table achievements add column if not exists rubies_used bigint not null default 0;
alter table achievements add column if not exists metal_used bigint not null default 0;
alter table achievements add column if not exists iron_used bigint not null default 0;
alter table achievements add column if not exists steel_used bigint not null default 0;
alter table achievements add column if not exists wood_used bigint not null default 0;
alter table achievements add column if not exists brown_coal_used bigint not null default 0;
alter table achievements add column if not exists black_coal_used bigint not null default 0;

alter table resources drop column if exists steam_used;
alter table resources drop column if exists premium_steam_used;
alter table resources drop column if exists cogwheels_used;
alter table resources drop column if exists premium_cogwheels_used;
alter table resources drop column if exists tokens_used;
alter table resources drop column if exists premium_tokens_used;
alter table resources drop column if exists coins_used;
alter table resources drop column if exists rubies_used;
alter table resources drop column if exists metal_used;
alter table resources drop column if exists iron_used;
alter table resources drop column if exists steel_used;
alter table resources drop column if exists wood_used;
alter table resources drop column if exists brown_coal_used;
alter table resources drop column if exists black_coal_used;
alter table resources drop column if exists simple_genome_used;
alter table resources drop column if exists common_genome_used;
alter table resources drop column if exists uncommon_genome_used;
alter table resources drop column if exists rare_genome_used;
alter table resources drop column if exists epic_genome_used;

alter table achievements add column if not exists merchant_items_bought integer not null default 0;
alter table achievements add column if not exists map_tiles_discovered integer not null default 0;
alter table achievements add column if not exists gear_modified integer not null default 0;
alter table achievements add column if not exists jewels_merged integer not null default 0;
alter table achievements add column if not exists buildings_upgrades_done integer not null default 0;
alter table achievements add column if not exists vehicles_upgrades_done integer not null default 0;
alter table achievements add column if not exists vehicle_part_upgrades_done integer not null default 0;
alter table achievements add column if not exists building_min_level integer not null default 0;
alter table achievements add column if not exists wooden_keys_collected bigint not null default 0;
alter table achievements add column if not exists bronze_keys_collected bigint not null default 0;
alter table achievements add column if not exists silver_keys_collected bigint not null default 0;
alter table achievements add column if not exists golden_keys_collected bigint not null default 0;
alter table achievements add column if not exists chests_opened bigint not null default 0;

alter table resources add column if not exists wooden_keys integer not null default 0;
alter table resources add column if not exists bronze_keys integer not null default 0;
alter table resources add column if not exists silver_keys integer not null default 0;
alter table resources add column if not exists golden_keys integer not null default 0;
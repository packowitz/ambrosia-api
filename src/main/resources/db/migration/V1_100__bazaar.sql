alter table progress add column if not exists trading_enabled boolean not null default false;
alter table progress add column if not exists black_market_enabled boolean not null default false;
alter table progress add column if not exists car_yard_enabled boolean not null default false;
alter table progress add column if not exists merchant_level int not null default 0;

alter table gear alter column player_id drop not null;

create table if not exists merchant_item (
    id bigserial primary key,
    merchant_level integer not null,
    sort_order integer not null,
    loot_box_id bigserial not null,
    price_type varchar(32) not null,
    price_amount int not null
);

drop table if exists merchant_player_item;
create table merchant_player_item (
    id bigserial primary key,
    player_id bigint not null,
    created timestamp not null,
    sort_order integer not null,
    merchant_level integer not null,
    sold boolean not null,
    price_type varchar(32) not null,
    price_amount integer not null,
    resource_type varchar(32),
    resource_amount integer,
    gear_id bigint,
    hero_base_id bigint,
    hero_level integer,
    jewel_type varchar(25),
    jewel_level integer,
    vehicle_base_id bigint,
    vehicle_part_type varchar(16),
    vehicle_part_quality varchar(16)
);
create index if not exists merchant_player_item_player_id_index on merchant_player_item (player_id);

delete from dynamic_property where type = 'BAZAAR_BUILDING';
insert into dynamic_property (category, type, level, progress_stat, value1, version) values
('BUILDING', 'BAZAAR_BUILDING', 1, 'MERCHANT_LEVEL', 1, 1),
('BUILDING', 'BAZAAR_BUILDING', 2, 'ENABLE_TRADING', 1, 1),
('BUILDING', 'BAZAAR_BUILDING', 3, 'ENABLE_BLACK_MARKET', 1, 1),
('BUILDING', 'BAZAAR_BUILDING', 4, 'MERCHANT_LEVEL', 1, 1),
('BUILDING', 'BAZAAR_BUILDING', 4, 'NEGOTIATION_LEVEL', 2, 1),
('BUILDING', 'BAZAAR_BUILDING', 5, 'ENABLE_CAR_YARD', 1, 1),
('BUILDING', 'BAZAAR_BUILDING', 6, 'NEGOTIATION_LEVEL', 2, 1),
('BUILDING', 'BAZAAR_BUILDING', 7, 'MERCHANT_LEVEL', 1, 1),
('BUILDING', 'BAZAAR_BUILDING', 8, 'NEGOTIATION_LEVEL', 3, 1),
('BUILDING', 'BAZAAR_BUILDING', 9, 'MERCHANT_LEVEL', 1, 1),
('BUILDING', 'BAZAAR_BUILDING', 10, 'NEGOTIATION_LEVEL', 3, 1);
delete from property_version where property_type = 'BAZAAR_BUILDING' and version = 1;
insert into property_version (property_type, version, active) values ('BAZAAR_BUILDING', 1, true);

update progress set merchant_level = 1 where player_id in (select player_id from building where type = 'BAZAAR')
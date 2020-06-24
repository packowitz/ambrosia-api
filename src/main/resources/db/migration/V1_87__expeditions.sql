alter table player drop column if exists current_map_id;
alter table progress add column if not exists expedition_level integer not null default 0;

create table if not exists expedition_base (
    id bigserial not null,
    loot_box_id bigint,
    name varchar(32) not null,
    description text not null,
    level int not null,
    rarity varchar(20) not null,
    duration_minutes integer not null,
    primary key (id)
);

alter table loot_item add column if not exists progress_stat varchar(32);
alter table loot_item add column if not exists progress_stat_bonus integer;

update loot_item set vehicle_part_type = 'ENGINE' where vehicle_part_type = '0';
update loot_item set vehicle_part_type = 'FRAME' where vehicle_part_type = '1';
update loot_item set vehicle_part_type = 'COMPUTER' where vehicle_part_type = '2';
update loot_item set vehicle_part_type = 'SMOKE_BOMB' where vehicle_part_type = '3';
update loot_item set vehicle_part_type = 'RAILGUN' where vehicle_part_type = '4';
update loot_item set vehicle_part_type = 'SNIPER_SCOPE' where vehicle_part_type = '5';
update loot_item set vehicle_part_type = 'MEDI_KIT' where vehicle_part_type = '6';
update loot_item set vehicle_part_type = 'REPAIR_KIT' where vehicle_part_type = '7';
update loot_item set vehicle_part_type = 'EXTRA_ARMOR' where vehicle_part_type = '8';
update loot_item set vehicle_part_type = 'NIGHT_VISION' where vehicle_part_type = '9';
update loot_item set vehicle_part_type = 'MAGNETIC_SHIELD' where vehicle_part_type = '10';
update loot_item set vehicle_part_type = 'MISSILE_DEFENSE' where vehicle_part_type = '11';

update loot_item set vehicle_part_quality = 'BASIC' where vehicle_part_quality = '0';
update loot_item set vehicle_part_quality = 'MODERATE' where vehicle_part_quality = '1';
update loot_item set vehicle_part_quality = 'GOOD' where vehicle_part_quality = '2';
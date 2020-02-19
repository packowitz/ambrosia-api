alter table vehicle_base add column if not exists avatar varchar(16) not null;
alter table vehicle_part add column if not exists quality varchar(16) not null;

alter table loot_item rename column resource_amount to resource_from;

alter table loot_item add column if not exists resource_to integer;
update loot_item set resource_to = resource_from where resource_from is not null;
alter table loot_item add column if not exists hero_level integer;
update loot_item set hero_level = 1 where hero_base_id is not null;
alter table loot_item add column if not exists jewel_type_names text;
alter table loot_item add column if not exists jewel_level integer;
alter table loot_item add column if not exists vehicle_base_id bigint;
alter table loot_item add column if not exists vehicle_part_type varchar(16);
alter table loot_item add column if not exists vehicle_part_quality varchar(16);
alter table vehicle_base add column if not exists special_part1 varchar(32);
alter table vehicle_base add column if not exists special_part2 varchar(32);
alter table vehicle_base add column if not exists special_part3 varchar(32);

update dynamic_property set type = concat(type, 'ERATE') where type like '%_MOD';

alter table upgrade add column if not exists jewel_type varchar(25);
alter table upgrade add column if not exists jewel_level int;

insert into dynamic_property (category, type, level, value1) values
('UPGRADE_TIME', 'JEWEL_UP_TIME', 2, 5),
('UPGRADE_TIME', 'JEWEL_UP_TIME', 3, 120),
('UPGRADE_TIME', 'JEWEL_UP_TIME', 4, 600),
('UPGRADE_TIME', 'JEWEL_UP_TIME', 5, 1800),
('UPGRADE_TIME', 'JEWEL_UP_TIME', 6, 3600),
('UPGRADE_TIME', 'JEWEL_UP_TIME', 7, 10800),
('UPGRADE_TIME', 'JEWEL_UP_TIME', 8, 21600),
('UPGRADE_TIME', 'JEWEL_UP_TIME', 9, 43200),
('UPGRADE_TIME', 'JEWEL_UP_TIME', 10, 86400);

insert into dynamic_property (category, type, level, resource_type, value1) values
('UPGRADE_COST', 'JEWEL_UP_COST', 2, 'COINS', 10),
('UPGRADE_COST', 'JEWEL_UP_COST', 3, 'COINS', 50),
('UPGRADE_COST', 'JEWEL_UP_COST', 4, 'COINS', 200),
('UPGRADE_COST', 'JEWEL_UP_COST', 5, 'COINS', 1000),
('UPGRADE_COST', 'JEWEL_UP_COST', 6, 'COINS', 5000),
('UPGRADE_COST', 'JEWEL_UP_COST', 7, 'COINS', 25000),
('UPGRADE_COST', 'JEWEL_UP_COST', 8, 'COINS', 100000),
('UPGRADE_COST', 'JEWEL_UP_COST', 9, 'COINS', 500000),
('UPGRADE_COST', 'JEWEL_UP_COST', 10, 'COINS', 1000000);

delete from dynamic_property where type like 'JEWELRY_UP_' and level = 10;

alter table progress add column if not exists max_jewel_upgrading_level int not null default 1;

insert into dynamic_property (category, type, level, value1) values
('BUILDING', 'JEWELRY_BUILDING', 1, 1),
('BUILDING', 'JEWELRY_BUILDING', 2, 2),
('BUILDING', 'JEWELRY_BUILDING', 3, 3),
('BUILDING', 'JEWELRY_BUILDING', 4, 4),
('BUILDING', 'JEWELRY_BUILDING', 5, 5),
('BUILDING', 'JEWELRY_BUILDING', 6, 6),
('BUILDING', 'JEWELRY_BUILDING', 7, 7),
('BUILDING', 'JEWELRY_BUILDING', 8, 8),
('BUILDING', 'JEWELRY_BUILDING', 9, 9);
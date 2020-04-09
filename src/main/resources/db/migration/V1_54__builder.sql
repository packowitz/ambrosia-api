create table if not exists builder_item (
  id bigserial,
  player_id bigint not null,
  position int not null,
  start_timestamp timestamp not null,
  finish_timestamp timestamp not null,
  resources text not null,
  building_type varchar(20),
  vehicle_id bigint,
  vehicle_part_id bigint,
  primary key (id)
);

alter table progress add column if not exists builder_queue_length int not null default 1;
alter table progress add column if not exists builder_speed int not null default 100;
alter table progress add column if not exists barrack_size int not null default 25;
alter table building add column if not exists upgrade_triggered boolean not null default false;
alter table vehicle add column if not exists upgrade_triggered boolean not null default false;
alter table vehicle_part add column if not exists upgrade_triggered boolean not null default false;

update building set type = 'STORAGE' where type = 'STORAGE_0';

alter table dynamic_property drop column if exists building_type;

insert into dynamic_property (category, type, level, value1) values
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 2, 600),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 3, 1800),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 4, 3600),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 5, 10800),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 6, 28800),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 7, 86400),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 8, 172800),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 9, 345600),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 10, 604800),

('BUILDING_UP_TIME', 'ARENA_UP_TIME', 2, 600),
('BUILDING_UP_TIME', 'ARENA_UP_TIME', 3, 1800),
('BUILDING_UP_TIME', 'ARENA_UP_TIME', 4, 3600),
('BUILDING_UP_TIME', 'ARENA_UP_TIME', 5, 10800),
('BUILDING_UP_TIME', 'ARENA_UP_TIME', 6, 28800),
('BUILDING_UP_TIME', 'ARENA_UP_TIME', 7, 86400),
('BUILDING_UP_TIME', 'ARENA_UP_TIME', 8, 172800),
('BUILDING_UP_TIME', 'ARENA_UP_TIME', 9, 345600),
('BUILDING_UP_TIME', 'ARENA_UP_TIME', 10, 604800),

('BUILDING_UP_TIME', 'BARRACKS_UP_TIME', 2, 600),
('BUILDING_UP_TIME', 'BARRACKS_UP_TIME', 3, 1800),
('BUILDING_UP_TIME', 'BARRACKS_UP_TIME', 4, 3600),
('BUILDING_UP_TIME', 'BARRACKS_UP_TIME', 5, 10800),
('BUILDING_UP_TIME', 'BARRACKS_UP_TIME', 6, 28800),
('BUILDING_UP_TIME', 'BARRACKS_UP_TIME', 7, 86400),
('BUILDING_UP_TIME', 'BARRACKS_UP_TIME', 8, 172800),
('BUILDING_UP_TIME', 'BARRACKS_UP_TIME', 9, 345600),
('BUILDING_UP_TIME', 'BARRACKS_UP_TIME', 10, 604800),

('BUILDING_UP_TIME', 'BAZAAR_UP_TIME', 2, 600),
('BUILDING_UP_TIME', 'BAZAAR_UP_TIME', 3, 1800),
('BUILDING_UP_TIME', 'BAZAAR_UP_TIME', 4, 3600),
('BUILDING_UP_TIME', 'BAZAAR_UP_TIME', 5, 10800),
('BUILDING_UP_TIME', 'BAZAAR_UP_TIME', 6, 28800),
('BUILDING_UP_TIME', 'BAZAAR_UP_TIME', 7, 86400),
('BUILDING_UP_TIME', 'BAZAAR_UP_TIME', 8, 172800),
('BUILDING_UP_TIME', 'BAZAAR_UP_TIME', 9, 345600),
('BUILDING_UP_TIME', 'BAZAAR_UP_TIME', 10, 604800),

('BUILDING_UP_TIME', 'FORGE_UP_TIME', 2, 600),
('BUILDING_UP_TIME', 'FORGE_UP_TIME', 3, 1800),
('BUILDING_UP_TIME', 'FORGE_UP_TIME', 4, 3600),
('BUILDING_UP_TIME', 'FORGE_UP_TIME', 5, 10800),
('BUILDING_UP_TIME', 'FORGE_UP_TIME', 6, 28800),
('BUILDING_UP_TIME', 'FORGE_UP_TIME', 7, 86400),
('BUILDING_UP_TIME', 'FORGE_UP_TIME', 8, 172800),
('BUILDING_UP_TIME', 'FORGE_UP_TIME', 9, 345600),
('BUILDING_UP_TIME', 'FORGE_UP_TIME', 10, 604800),

('BUILDING_UP_TIME', 'GARAGE_UP_TIME', 2, 600),
('BUILDING_UP_TIME', 'GARAGE_UP_TIME', 3, 1800),
('BUILDING_UP_TIME', 'GARAGE_UP_TIME', 4, 3600),
('BUILDING_UP_TIME', 'GARAGE_UP_TIME', 5, 10800),
('BUILDING_UP_TIME', 'GARAGE_UP_TIME', 6, 28800),
('BUILDING_UP_TIME', 'GARAGE_UP_TIME', 7, 86400),
('BUILDING_UP_TIME', 'GARAGE_UP_TIME', 8, 172800),
('BUILDING_UP_TIME', 'GARAGE_UP_TIME', 9, 345600),
('BUILDING_UP_TIME', 'GARAGE_UP_TIME', 10, 604800),

('BUILDING_UP_TIME', 'JEWELRY_UP_TIME', 2, 600),
('BUILDING_UP_TIME', 'JEWELRY_UP_TIME', 3, 1800),
('BUILDING_UP_TIME', 'JEWELRY_UP_TIME', 4, 3600),
('BUILDING_UP_TIME', 'JEWELRY_UP_TIME', 5, 10800),
('BUILDING_UP_TIME', 'JEWELRY_UP_TIME', 6, 28800),
('BUILDING_UP_TIME', 'JEWELRY_UP_TIME', 7, 86400),
('BUILDING_UP_TIME', 'JEWELRY_UP_TIME', 8, 172800),
('BUILDING_UP_TIME', 'JEWELRY_UP_TIME', 9, 345600),
('BUILDING_UP_TIME', 'JEWELRY_UP_TIME', 10, 604800),

('BUILDING_UP_TIME', 'LABORATORY_UP_TIME', 2, 600),
('BUILDING_UP_TIME', 'LABORATORY_UP_TIME', 3, 1800),
('BUILDING_UP_TIME', 'LABORATORY_UP_TIME', 4, 3600),
('BUILDING_UP_TIME', 'LABORATORY_UP_TIME', 5, 10800),
('BUILDING_UP_TIME', 'LABORATORY_UP_TIME', 6, 28800),
('BUILDING_UP_TIME', 'LABORATORY_UP_TIME', 7, 86400),
('BUILDING_UP_TIME', 'LABORATORY_UP_TIME', 8, 172800),
('BUILDING_UP_TIME', 'LABORATORY_UP_TIME', 9, 345600),
('BUILDING_UP_TIME', 'LABORATORY_UP_TIME', 10, 604800),

('BUILDING_UP_TIME', 'STORAGE_UP_TIME', 2, 600),
('BUILDING_UP_TIME', 'STORAGE_UP_TIME', 3, 1800),
('BUILDING_UP_TIME', 'STORAGE_UP_TIME', 4, 3600),
('BUILDING_UP_TIME', 'STORAGE_UP_TIME', 5, 10800),
('BUILDING_UP_TIME', 'STORAGE_UP_TIME', 6, 28800),
('BUILDING_UP_TIME', 'STORAGE_UP_TIME', 7, 86400),
('BUILDING_UP_TIME', 'STORAGE_UP_TIME', 8, 172800),
('BUILDING_UP_TIME', 'STORAGE_UP_TIME', 9, 345600),
('BUILDING_UP_TIME', 'STORAGE_UP_TIME', 10, 604800);

insert into dynamic_property (category, type, level, resource_type, value1) values
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 2, 'METAL', 50),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 3, 'METAL', 200),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 3, 'IRON', 20),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 4, 'METAL', 500),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 4, 'IRON', 150),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 5, 'METAL', 1000),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 5, 'IRON', 300),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 5, 'STEAL', 25),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 6, 'METAL', 1500),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 6, 'IRON', 500),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 6, 'STEAL', 75),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 7, 'METAL', 2000),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 7, 'IRON', 1000),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 7, 'STEAL', 250),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 8, 'METAL', 2500),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 8, 'IRON', 1500),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 8, 'STEAL', 500),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 9, 'METAL', 5000),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 9, 'IRON', 3000),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 9, 'STEAL', 2000),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 10, 'METAL', 10000),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 10, 'IRON', 7500),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 10, 'STEAL', 5000),

('BUILDING_UP_COST', 'ARENA_UP_COST', 2, 'METAL', 50),
('BUILDING_UP_COST', 'ARENA_UP_COST', 3, 'METAL', 200),
('BUILDING_UP_COST', 'ARENA_UP_COST', 3, 'IRON', 20),
('BUILDING_UP_COST', 'ARENA_UP_COST', 4, 'METAL', 500),
('BUILDING_UP_COST', 'ARENA_UP_COST', 4, 'IRON', 150),
('BUILDING_UP_COST', 'ARENA_UP_COST', 5, 'METAL', 1000),
('BUILDING_UP_COST', 'ARENA_UP_COST', 5, 'IRON', 300),
('BUILDING_UP_COST', 'ARENA_UP_COST', 5, 'STEAL', 25),
('BUILDING_UP_COST', 'ARENA_UP_COST', 6, 'METAL', 1500),
('BUILDING_UP_COST', 'ARENA_UP_COST', 6, 'IRON', 500),
('BUILDING_UP_COST', 'ARENA_UP_COST', 6, 'STEAL', 75),
('BUILDING_UP_COST', 'ARENA_UP_COST', 7, 'METAL', 2000),
('BUILDING_UP_COST', 'ARENA_UP_COST', 7, 'IRON', 1000),
('BUILDING_UP_COST', 'ARENA_UP_COST', 7, 'STEAL', 250),
('BUILDING_UP_COST', 'ARENA_UP_COST', 8, 'METAL', 2500),
('BUILDING_UP_COST', 'ARENA_UP_COST', 8, 'IRON', 1500),
('BUILDING_UP_COST', 'ARENA_UP_COST', 8, 'STEAL', 500),
('BUILDING_UP_COST', 'ARENA_UP_COST', 9, 'METAL', 5000),
('BUILDING_UP_COST', 'ARENA_UP_COST', 9, 'IRON', 3000),
('BUILDING_UP_COST', 'ARENA_UP_COST', 9, 'STEAL', 2000),
('BUILDING_UP_COST', 'ARENA_UP_COST', 10, 'METAL', 10000),
('BUILDING_UP_COST', 'ARENA_UP_COST', 10, 'IRON', 7500),
('BUILDING_UP_COST', 'ARENA_UP_COST', 10, 'STEAL', 5000),

('BUILDING_UP_COST', 'BARRACKS_UP_COST', 2, 'METAL', 50),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 3, 'METAL', 200),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 3, 'IRON', 20),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 4, 'METAL', 500),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 4, 'IRON', 150),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 5, 'METAL', 1000),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 5, 'IRON', 300),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 5, 'STEAL', 25),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 6, 'METAL', 1500),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 6, 'IRON', 500),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 6, 'STEAL', 75),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 7, 'METAL', 2000),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 7, 'IRON', 1000),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 7, 'STEAL', 250),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 8, 'METAL', 2500),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 8, 'IRON', 1500),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 8, 'STEAL', 500),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 9, 'METAL', 5000),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 9, 'IRON', 3000),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 9, 'STEAL', 2000),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 10, 'METAL', 10000),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 10, 'IRON', 7500),
('BUILDING_UP_COST', 'BARRACKS_UP_COST', 10, 'STEAL', 5000),

('BUILDING_UP_COST', 'BAZAAR_UP_COST', 2, 'METAL', 50),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 3, 'METAL', 200),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 3, 'IRON', 20),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 4, 'METAL', 500),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 4, 'IRON', 150),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 5, 'METAL', 1000),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 5, 'IRON', 300),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 5, 'STEAL', 25),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 6, 'METAL', 1500),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 6, 'IRON', 500),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 6, 'STEAL', 75),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 7, 'METAL', 2000),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 7, 'IRON', 1000),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 7, 'STEAL', 250),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 8, 'METAL', 2500),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 8, 'IRON', 1500),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 8, 'STEAL', 500),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 9, 'METAL', 5000),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 9, 'IRON', 3000),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 9, 'STEAL', 2000),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 10, 'METAL', 10000),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 10, 'IRON', 7500),
('BUILDING_UP_COST', 'BAZAAR_UP_COST', 10, 'STEAL', 5000),

('BUILDING_UP_COST', 'FORGE_UP_COST', 2, 'METAL', 50),
('BUILDING_UP_COST', 'FORGE_UP_COST', 3, 'METAL', 200),
('BUILDING_UP_COST', 'FORGE_UP_COST', 3, 'IRON', 20),
('BUILDING_UP_COST', 'FORGE_UP_COST', 4, 'METAL', 500),
('BUILDING_UP_COST', 'FORGE_UP_COST', 4, 'IRON', 150),
('BUILDING_UP_COST', 'FORGE_UP_COST', 5, 'METAL', 1000),
('BUILDING_UP_COST', 'FORGE_UP_COST', 5, 'IRON', 300),
('BUILDING_UP_COST', 'FORGE_UP_COST', 5, 'STEAL', 25),
('BUILDING_UP_COST', 'FORGE_UP_COST', 6, 'METAL', 1500),
('BUILDING_UP_COST', 'FORGE_UP_COST', 6, 'IRON', 500),
('BUILDING_UP_COST', 'FORGE_UP_COST', 6, 'STEAL', 75),
('BUILDING_UP_COST', 'FORGE_UP_COST', 7, 'METAL', 2000),
('BUILDING_UP_COST', 'FORGE_UP_COST', 7, 'IRON', 1000),
('BUILDING_UP_COST', 'FORGE_UP_COST', 7, 'STEAL', 250),
('BUILDING_UP_COST', 'FORGE_UP_COST', 8, 'METAL', 2500),
('BUILDING_UP_COST', 'FORGE_UP_COST', 8, 'IRON', 1500),
('BUILDING_UP_COST', 'FORGE_UP_COST', 8, 'STEAL', 500),
('BUILDING_UP_COST', 'FORGE_UP_COST', 9, 'METAL', 5000),
('BUILDING_UP_COST', 'FORGE_UP_COST', 9, 'IRON', 3000),
('BUILDING_UP_COST', 'FORGE_UP_COST', 9, 'STEAL', 2000),
('BUILDING_UP_COST', 'FORGE_UP_COST', 10, 'METAL', 10000),
('BUILDING_UP_COST', 'FORGE_UP_COST', 10, 'IRON', 7500),
('BUILDING_UP_COST', 'FORGE_UP_COST', 10, 'STEAL', 5000),

('BUILDING_UP_COST', 'GARAGE_UP_COST', 2, 'METAL', 50),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 3, 'METAL', 200),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 3, 'IRON', 20),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 4, 'METAL', 500),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 4, 'IRON', 150),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 5, 'METAL', 1000),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 5, 'IRON', 300),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 5, 'STEAL', 25),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 6, 'METAL', 1500),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 6, 'IRON', 500),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 6, 'STEAL', 75),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 7, 'METAL', 2000),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 7, 'IRON', 1000),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 7, 'STEAL', 250),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 8, 'METAL', 2500),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 8, 'IRON', 1500),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 8, 'STEAL', 500),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 9, 'METAL', 5000),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 9, 'IRON', 3000),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 9, 'STEAL', 2000),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 10, 'METAL', 10000),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 10, 'IRON', 7500),
('BUILDING_UP_COST', 'GARAGE_UP_COST', 10, 'STEAL', 5000),

('BUILDING_UP_COST', 'JEWELRY_UP_COST', 2, 'METAL', 50),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 3, 'METAL', 200),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 3, 'IRON', 20),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 4, 'METAL', 500),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 4, 'IRON', 150),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 5, 'METAL', 1000),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 5, 'IRON', 300),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 5, 'STEAL', 25),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 6, 'METAL', 1500),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 6, 'IRON', 500),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 6, 'STEAL', 75),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 7, 'METAL', 2000),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 7, 'IRON', 1000),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 7, 'STEAL', 250),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 8, 'METAL', 2500),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 8, 'IRON', 1500),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 8, 'STEAL', 500),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 9, 'METAL', 5000),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 9, 'IRON', 3000),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 9, 'STEAL', 2000),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 10, 'METAL', 10000),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 10, 'IRON', 7500),
('BUILDING_UP_COST', 'JEWELRY_UP_COST', 10, 'STEAL', 5000),

('BUILDING_UP_COST', 'LABORATORY_UP_COST', 2, 'METAL', 50),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 3, 'METAL', 200),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 3, 'IRON', 20),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 4, 'METAL', 500),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 4, 'IRON', 150),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 5, 'METAL', 1000),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 5, 'IRON', 300),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 5, 'STEAL', 25),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 6, 'METAL', 1500),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 6, 'IRON', 500),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 6, 'STEAL', 75),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 7, 'METAL', 2000),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 7, 'IRON', 1000),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 7, 'STEAL', 250),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 8, 'METAL', 2500),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 8, 'IRON', 1500),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 8, 'STEAL', 500),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 9, 'METAL', 5000),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 9, 'IRON', 3000),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 9, 'STEAL', 2000),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 10, 'METAL', 10000),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 10, 'IRON', 7500),
('BUILDING_UP_COST', 'LABORATORY_UP_COST', 10, 'STEAL', 5000),

('BUILDING_UP_COST', 'STORAGE_UP_COST', 2, 'METAL', 50),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 3, 'METAL', 200),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 3, 'IRON', 20),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 4, 'METAL', 500),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 4, 'IRON', 150),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 5, 'METAL', 1000),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 5, 'IRON', 300),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 5, 'STEAL', 25),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 6, 'METAL', 1500),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 6, 'IRON', 500),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 6, 'STEAL', 75),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 7, 'METAL', 2000),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 7, 'IRON', 1000),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 7, 'STEAL', 250),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 8, 'METAL', 2500),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 8, 'IRON', 1500),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 8, 'STEAL', 500),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 9, 'METAL', 5000),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 9, 'IRON', 3000),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 9, 'STEAL', 2000),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 10, 'METAL', 10000),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 10, 'IRON', 7500),
('BUILDING_UP_COST', 'STORAGE_UP_COST', 10, 'STEAL', 5000);


insert into dynamic_property (category, type, level, value1) values
('BUILDING', 'BARRACKS_BUILDING', 1, 25),
('BUILDING', 'BARRACKS_BUILDING', 2, 5),
('BUILDING', 'BARRACKS_BUILDING', 3, 5),
('BUILDING', 'BARRACKS_BUILDING', 4, 7),
('BUILDING', 'BARRACKS_BUILDING', 5, 7),
('BUILDING', 'BARRACKS_BUILDING', 6, 8),
('BUILDING', 'BARRACKS_BUILDING', 7, 8),
('BUILDING', 'BARRACKS_BUILDING', 8, 10),
('BUILDING', 'BARRACKS_BUILDING', 9, 10),
('BUILDING', 'BARRACKS_BUILDING', 10, 15);
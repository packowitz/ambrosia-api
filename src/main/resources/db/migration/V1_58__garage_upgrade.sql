alter table progress add column vehicle_storage int not null default 2;
alter table progress add column vehicle_part_storage int not null default 20;

insert into dynamic_property (category, type, level, value1, value2) values
('BUILDING', 'GARAGE_BUILDING', 1, 2, 20),
('BUILDING', 'GARAGE_BUILDING', 2, 1, 5),
('BUILDING', 'GARAGE_BUILDING', 3, 1, 5),
('BUILDING', 'GARAGE_BUILDING', 4, 1, 5),
('BUILDING', 'GARAGE_BUILDING', 5, 1, 5),
('BUILDING', 'GARAGE_BUILDING', 6, 1, 5),
('BUILDING', 'GARAGE_BUILDING', 7, 1, 5),
('BUILDING', 'GARAGE_BUILDING', 8, 1, 5),
('BUILDING', 'GARAGE_BUILDING', 9, 1, 5),
('BUILDING', 'GARAGE_BUILDING', 10, 2, 10);
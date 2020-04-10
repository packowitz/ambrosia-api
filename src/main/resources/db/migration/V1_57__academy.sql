alter table progress add column max_training_level int not null default 10;

delete from dynamic_property where type = 'ACADEMY_UP_TIME';
delete from dynamic_property where type = 'ACADEMY_UP_COST';


insert into dynamic_property (category, type, level, value1) values
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 2, 1800),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 3, 3600),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 4, 10800),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 5, 86400),
('BUILDING_UP_TIME', 'ACADEMY_UP_TIME', 6, 345600);

insert into dynamic_property (category, type, level, resource_type, value1) values
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 2, 'METAL', 70),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 3, 'METAL', 250),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 3, 'IRON', 30),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 4, 'METAL', 600),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 4, 'IRON', 200),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 5, 'METAL', 1250),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 5, 'IRON', 400),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 5, 'STEAL', 40),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 6, 'METAL', 2000),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 6, 'IRON', 1000),
('BUILDING_UP_COST', 'ACADEMY_UP_COST', 6, 'STEAL', 150);

insert into dynamic_property (category, type, level, value1) values
('BUILDING', 'ACADEMY_BUILDING', 1, 10),
('BUILDING', 'ACADEMY_BUILDING', 2, 20),
('BUILDING', 'ACADEMY_BUILDING', 3, 30),
('BUILDING', 'ACADEMY_BUILDING', 4, 40),
('BUILDING', 'ACADEMY_BUILDING', 5, 50),
('BUILDING', 'ACADEMY_BUILDING', 6, 60);
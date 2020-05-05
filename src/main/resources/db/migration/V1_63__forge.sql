alter table progress add column if not exists gear_modification_rarity int not null default 1;
alter table progress add column if not exists gear_modification_speed int not null default 100;
alter table progress add column if not exists gear_break_down_rarity int not null default 1;
alter table progress add column if not exists gear_break_down_resources_inc int not null default 0;
alter table progress add column if not exists re_roll_gear_quality_enabled boolean not null default true;
alter table progress add column if not exists re_roll_gear_stat_enabled boolean not null default false;
alter table progress add column if not exists inc_gear_rarity_enabled boolean not null default false;
alter table progress add column if not exists re_roll_gear_jewel_enabled boolean not null default false;
alter table progress add column if not exists add_gear_jewel_enabled boolean not null default false;
alter table progress add column if not exists add_gear_special_jewel_enabled boolean not null default false;

alter table gear add column if not exists modification_in_progress boolean not null default false;
alter table gear add column if not exists modification_performed boolean not null default false;
alter table gear add column if not exists modification_allowed varchar(25);

alter table upgrade add column if not exists gear_modification varchar(25);
alter table upgrade add column if not exists gear_id bigint;

insert into dynamic_property (category, type, level, value1) values
('BUILDING', 'FORGE_MOD_RARITY', 1, 1),
('BUILDING', 'FORGE_MOD_RARITY', 2, 2),
('BUILDING', 'FORGE_MOD_RARITY', 4, 3),
('BUILDING', 'FORGE_MOD_RARITY', 6, 4),
('BUILDING', 'FORGE_MOD_RARITY', 8, 5),
('BUILDING', 'FORGE_MOD_RARITY', 10, 6),

('BUILDING', 'FORGE_MOD_SPEED', 3, 20),
('BUILDING', 'FORGE_MOD_SPEED', 5, 20),
('BUILDING', 'FORGE_MOD_SPEED', 7, 20),
('BUILDING', 'FORGE_MOD_SPEED', 9, 20),
('BUILDING', 'FORGE_MOD_SPEED', 10, 20),

('BUILDING', 'FORGE_BREAKDOWN_RARITY', 1, 1),
('BUILDING', 'FORGE_BREAKDOWN_RARITY', 2, 2),
('BUILDING', 'FORGE_BREAKDOWN_RARITY', 3, 3),
('BUILDING', 'FORGE_BREAKDOWN_RARITY', 4, 4),
('BUILDING', 'FORGE_BREAKDOWN_RARITY', 5, 5),
('BUILDING', 'FORGE_BREAKDOWN_RARITY', 6, 6),

('BUILDING', 'FORGE_BREAKDOWN_RES', 2, 10),
('BUILDING', 'FORGE_BREAKDOWN_RES', 4, 10),
('BUILDING', 'FORGE_BREAKDOWN_RES', 6, 10),
('BUILDING', 'FORGE_BREAKDOWN_RES', 8, 10),
('BUILDING', 'FORGE_BREAKDOWN_RES', 10, 10),

('BUILDING', 'FORGE_REROLL_QUAL', 1, 1),
('BUILDING', 'FORGE_REROLL_STAT', 3, 1),
('BUILDING', 'FORGE_INC_RARITY', 7, 1),
('BUILDING', 'FORGE_REROLL_JEWEL', 8, 1),
('BUILDING', 'FORGE_ADD_JEWEL', 9, 1),
('BUILDING', 'FORGE_ADD_SP_JEWEL', 10, 1);

insert into dynamic_property (category, type, level, resource_type, value1, value2) values
('BUILDING', 'FORGE_BREAKDOWN_0_JEWEL', 1, 'METAL', 1, 2),
('BUILDING', 'FORGE_BREAKDOWN_1_JEWEL', 1, 'METAL', 1, 3),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 1, 'METAL', 2, 4),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 1, 'METAL', 2, 5),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 1, 'METAL', 3, 6),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 1, 'METAL', 4, 7),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 1, 'IRON', 1, 2),

('BUILDING', 'FORGE_BREAKDOWN_0_JEWEL', 2, 'METAL', 2, 4),
('BUILDING', 'FORGE_BREAKDOWN_1_JEWEL', 2, 'METAL', 2, 5),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 2, 'METAL', 3, 6),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 2, 'METAL', 3, 7),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 2, 'METAL', 4, 8),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 2, 'IRON', 1, 2),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 2, 'METAL', 5, 9),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 2, 'IRON', 1, 3),

('BUILDING', 'FORGE_BREAKDOWN_0_JEWEL', 3, 'METAL', 4, 6),
('BUILDING', 'FORGE_BREAKDOWN_1_JEWEL', 3, 'METAL', 5, 7),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 3, 'METAL', 6, 8),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 3, 'METAL', 6, 9),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 3, 'IRON', 1, 3),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 3, 'METAL', 7, 10),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 3, 'IRON', 2, 4),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 3, 'METAL', 8, 11),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 3, 'IRON', 3, 5),

('BUILDING', 'FORGE_BREAKDOWN_0_JEWEL', 4, 'METAL', 5, 8),
('BUILDING', 'FORGE_BREAKDOWN_1_JEWEL', 4, 'METAL', 5, 9),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 4, 'METAL', 6, 10),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 4, 'IRON', 2, 4),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 4, 'METAL', 7, 11),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 4, 'IRON', 3, 5),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 4, 'METAL', 8, 12),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 4, 'IRON', 4, 6),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 4, 'STEAL', 1, 2),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 4, 'METAL', 9, 13),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 4, 'IRON', 5, 7),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 4, 'STEAL', 1, 3),

('BUILDING', 'FORGE_BREAKDOWN_0_JEWEL', 5, 'METAL', 6, 10),
('BUILDING', 'FORGE_BREAKDOWN_0_JEWEL', 5, 'IRON', 1, 2),
('BUILDING', 'FORGE_BREAKDOWN_1_JEWEL', 5, 'METAL', 7, 11),
('BUILDING', 'FORGE_BREAKDOWN_1_JEWEL', 5, 'IRON', 2, 4),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 5, 'METAL', 8, 12),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 5, 'IRON', 3, 5),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 5, 'STEAL', 1, 2),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 5, 'METAL', 9, 13),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 5, 'IRON', 4, 6),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 5, 'STEAL', 1, 3),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 5, 'METAL', 10, 14),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 5, 'IRON', 5, 7),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 5, 'STEAL', 2, 4),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 5, 'METAL', 11, 15),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 5, 'IRON', 6, 8),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 5, 'STEAL', 3, 5),

('BUILDING', 'FORGE_BREAKDOWN_0_JEWEL', 6, 'METAL', 7, 12),
('BUILDING', 'FORGE_BREAKDOWN_0_JEWEL', 6, 'IRON', 2, 4),
('BUILDING', 'FORGE_BREAKDOWN_0_JEWEL', 6, 'STEAL', 1, 2),
('BUILDING', 'FORGE_BREAKDOWN_1_JEWEL', 6, 'METAL', 8, 13),
('BUILDING', 'FORGE_BREAKDOWN_1_JEWEL', 6, 'IRON', 3, 5),
('BUILDING', 'FORGE_BREAKDOWN_1_JEWEL', 6, 'STEAL', 1, 3),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 6, 'METAL', 9, 14),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 6, 'IRON', 5, 7),
('BUILDING', 'FORGE_BREAKDOWN_2_JEWEL', 6, 'STEAL', 2, 4),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 6, 'METAL', 10, 15),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 6, 'IRON', 6, 8),
('BUILDING', 'FORGE_BREAKDOWN_3_JEWEL', 6, 'STEAL', 2, 5),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 6, 'METAL', 11, 16),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 6, 'IRON', 7, 10),
('BUILDING', 'FORGE_BREAKDOWN_4_JEWEL', 6, 'STEAL', 3, 6),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 6, 'METAL', 12, 18),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 6, 'IRON', 8, 12),
('BUILDING', 'FORGE_BREAKDOWN_5_JEWEL', 6, 'STEAL', 5, 9);

insert into dynamic_property (category, type, level, value1) values
('UPGRADE_TIME', 'GEAR_QUAL_UP_TIME', 1, 30),
('UPGRADE_TIME', 'GEAR_QUAL_UP_TIME', 2, 60),
('UPGRADE_TIME', 'GEAR_QUAL_UP_TIME', 3, 120),
('UPGRADE_TIME', 'GEAR_QUAL_UP_TIME', 4, 300),
('UPGRADE_TIME', 'GEAR_QUAL_UP_TIME', 5, 480),
('UPGRADE_TIME', 'GEAR_QUAL_UP_TIME', 6, 600),

('UPGRADE_TIME', 'GEAR_STAT_UP_TIME', 1, 120),
('UPGRADE_TIME', 'GEAR_STAT_UP_TIME', 2, 300),
('UPGRADE_TIME', 'GEAR_STAT_UP_TIME', 3, 600),
('UPGRADE_TIME', 'GEAR_STAT_UP_TIME', 4, 1200),
('UPGRADE_TIME', 'GEAR_STAT_UP_TIME', 5, 1800),
('UPGRADE_TIME', 'GEAR_STAT_UP_TIME', 6, 3600),

('UPGRADE_TIME', 'GEAR_INC_UP_TIME', 2, 3600),
('UPGRADE_TIME', 'GEAR_INC_UP_TIME', 3, 10800),
('UPGRADE_TIME', 'GEAR_INC_UP_TIME', 4, 43200),
('UPGRADE_TIME', 'GEAR_INC_UP_TIME', 5, 86400),
('UPGRADE_TIME', 'GEAR_INC_UP_TIME', 6, 259200),

('UPGRADE_TIME', 'GEAR_ADD_JEWEL_UP_TIME', 1, 300),
('UPGRADE_TIME', 'GEAR_ADD_JEWEL_UP_TIME', 2, 1800),
('UPGRADE_TIME', 'GEAR_ADD_JEWEL_UP_TIME', 3, 3600),
('UPGRADE_TIME', 'GEAR_ADD_JEWEL_UP_TIME', 4, 14400),
('UPGRADE_TIME', 'GEAR_ADD_JEWEL_UP_TIME', 5, 43200),
('UPGRADE_TIME', 'GEAR_ADD_JEWEL_UP_TIME', 6, 86400),

('UPGRADE_TIME', 'GEAR_JEWEL_UP_TIME', 1, 120),
('UPGRADE_TIME', 'GEAR_JEWEL_UP_TIME', 2, 300),
('UPGRADE_TIME', 'GEAR_JEWEL_UP_TIME', 3, 600),
('UPGRADE_TIME', 'GEAR_JEWEL_UP_TIME', 4, 1200),
('UPGRADE_TIME', 'GEAR_JEWEL_UP_TIME', 5, 1800),
('UPGRADE_TIME', 'GEAR_JEWEL_UP_TIME', 6, 3600),

('UPGRADE_TIME', 'GEAR_ADD_SPECIAL_UP_TIME', 1, 300),
('UPGRADE_TIME', 'GEAR_ADD_SPECIAL_UP_TIME', 2, 1800),
('UPGRADE_TIME', 'GEAR_ADD_SPECIAL_UP_TIME', 3, 3600),
('UPGRADE_TIME', 'GEAR_ADD_SPECIAL_UP_TIME', 4, 14400),
('UPGRADE_TIME', 'GEAR_ADD_SPECIAL_UP_TIME', 5, 43200),
('UPGRADE_TIME', 'GEAR_ADD_SPECIAL_UP_TIME', 6, 86400);


insert into dynamic_property (category, type, level, resource_type, value1) values
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 1, 'WOOD', 10),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 2, 'WOOD', 40),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 2, 'BROWN_COAL', 10),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 3, 'WOOD', 150),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 3, 'BROWN_COAL', 30),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 4, 'WOOD', 500),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 4, 'BROWN_COAL', 100),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 4, 'BLACK_COAL', 10),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 5, 'WOOD', 2000),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 5, 'BROWN_COAL', 200),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 5, 'BLACK_COAL', 20),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 6, 'WOOD', 5000),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 6, 'BROWN_COAL', 500),
('UPGRADE_COST', 'GEAR_QUAL_UP_COST', 6, 'BLACK_COAL', 50),

('UPGRADE_COST', 'GEAR_STAT_UP_COST', 1, 'WOOD', 25),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 2, 'WOOD', 100),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 2, 'BROWN_COAL', 20),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 3, 'WOOD', 350),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 3, 'BROWN_COAL', 50),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 4, 'WOOD', 1000),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 4, 'BROWN_COAL', 150),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 4, 'BLACK_COAL', 15),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 5, 'WOOD', 3000),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 5, 'BROWN_COAL', 350),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 5, 'BLACK_COAL', 45),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 6, 'WOOD', 10000),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 6, 'BROWN_COAL', 800),
('UPGRADE_COST', 'GEAR_STAT_UP_COST', 6, 'BLACK_COAL', 150),

('UPGRADE_COST', 'GEAR_INC_UP_COST', 2, 'WOOD', 200),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 2, 'BROWN_COAL', 50),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 3, 'WOOD', 750),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 3, 'BROWN_COAL', 250),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 4, 'WOOD', 3000),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 4, 'BROWN_COAL', 1000),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 4, 'BLACK_COAL', 50),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 5, 'WOOD', 5000),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 5, 'BROWN_COAL', 3000),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 5, 'BLACK_COAL', 250),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 6, 'WOOD', 10000),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 6, 'BROWN_COAL', 5000),
('UPGRADE_COST', 'GEAR_INC_UP_COST', 6, 'BLACK_COAL', 1000),

('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 1, 'WOOD', 30),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 2, 'WOOD', 100),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 2, 'BROWN_COAL', 20),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 3, 'WOOD', 300),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 3, 'BROWN_COAL', 50),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 4, 'WOOD', 1000),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 4, 'BROWN_COAL', 150),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 4, 'BLACK_COAL', 15),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 5, 'WOOD', 3000),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 5, 'BROWN_COAL', 350),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 5, 'BLACK_COAL', 45),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 6, 'WOOD', 10000),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 6, 'BROWN_COAL', 800),
('UPGRADE_COST', 'GEAR_ADD_JEWEL_UP_COST', 6, 'BLACK_COAL', 150),

('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 1, 'WOOD', 25),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 2, 'WOOD', 100),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 2, 'BROWN_COAL', 20),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 3, 'WOOD', 350),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 3, 'BROWN_COAL', 50),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 4, 'WOOD', 1000),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 4, 'BROWN_COAL', 150),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 4, 'BLACK_COAL', 15),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 5, 'WOOD', 3000),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 5, 'BROWN_COAL', 350),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 5, 'BLACK_COAL', 45),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 6, 'WOOD', 10000),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 6, 'BROWN_COAL', 800),
('UPGRADE_COST', 'GEAR_JEWEL_UP_COST', 6, 'BLACK_COAL', 150),

('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 1, 'WOOD', 30),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 2, 'WOOD', 100),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 2, 'BROWN_COAL', 20),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 3, 'WOOD', 300),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 3, 'BROWN_COAL', 50),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 4, 'WOOD', 1000),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 4, 'BROWN_COAL', 150),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 4, 'BLACK_COAL', 15),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 5, 'WOOD', 3000),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 5, 'BROWN_COAL', 350),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 5, 'BLACK_COAL', 45),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 6, 'WOOD', 10000),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 6, 'BROWN_COAL', 800),
('UPGRADE_COST', 'GEAR_ADD_SPECIAL_UP_COST', 6, 'BLACK_COAL', 150);
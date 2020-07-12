alter table progress add column if not exists vip_points int not null default 0;
alter table progress add column if not exists vip_level int not null default 0;
alter table progress add column if not exists vip_max_points int not null default 100;
alter table progress add column if not exists training_xp_boost int not null default 0;
alter table progress add column if not exists training_asc_boost int not null default 0;
alter table progress add column if not exists gear_quality_increase int not null default 0;
alter table progress add column if not exists uncommon_starting_level int not null default 1;

alter table resources add column if not exists resource_generation_speed int not null default 100;

delete from dynamic_property where type = 'ACADEMY_UP_TIME' and version = 2;
insert into dynamic_property (category, type, level, value1, version) values
('UPGRADE_TIME', 'ACADEMY_UP_TIME', 2, 600, 2),
('UPGRADE_TIME', 'ACADEMY_UP_TIME', 3, 1800, 2),
('UPGRADE_TIME', 'ACADEMY_UP_TIME', 4, 3600, 2),
('UPGRADE_TIME', 'ACADEMY_UP_TIME', 5, 10800, 2),
('UPGRADE_TIME', 'ACADEMY_UP_TIME', 6, 28800, 2),
('UPGRADE_TIME', 'ACADEMY_UP_TIME', 7, 86400, 2),
('UPGRADE_TIME', 'ACADEMY_UP_TIME', 8, 172800, 2),
('UPGRADE_TIME', 'ACADEMY_UP_TIME', 9, 345600, 2),
('UPGRADE_TIME', 'ACADEMY_UP_TIME', 10, 604800, 2);
delete from property_version where property_type = 'ACADEMY_UP_TIME' and version = 2;
update property_version set active = false where property_type = 'ACADEMY_UP_TIME';
insert into property_version (property_type, version, active) values ('ACADEMY_UP_TIME', 2, true);

delete from dynamic_property where type = 'ACADEMY_UP_COST' and version = 2;
insert into dynamic_property (category, type, level, resource_type, value1, version) values
('UPGRADE_COST', 'ACADEMY_UP_COST', 2, 'METAL', 50, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 3, 'METAL', 200, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 3, 'IRON', 20, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 4, 'METAL', 500, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 4, 'IRON', 150, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 5, 'METAL', 1000, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 5, 'IRON', 300, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 5, 'STEEL', 25, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 6, 'METAL', 1500, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 6, 'IRON', 500, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 6, 'STEEL', 75, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 7, 'METAL', 2000, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 7, 'IRON', 1000, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 7, 'STEEL', 250, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 8, 'METAL', 2500, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 8, 'IRON', 1500, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 8, 'STEEL', 500, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 9, 'METAL', 5000, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 9, 'IRON', 3000, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 9, 'STEEL', 2000, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 10, 'METAL', 10000, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 10, 'IRON', 7500, 2),
('UPGRADE_COST', 'ACADEMY_UP_COST', 10, 'STEEL', 5000, 2);
delete from property_version where property_type = 'ACADEMY_UP_COST' and version = 2;
update property_version set active = false where property_type = 'ACADEMY_UP_COST';
insert into property_version (property_type, version, active) values ('ACADEMY_UP_COST', 2, true);

delete from dynamic_property where type = 'ACADEMY_BUILDING' and version = 2;
insert into dynamic_property (category, type, level, progress_stat, value1, version) values
('BUILDING', 'ACADEMY_BUILDING', 1, 'HERO_TRAIN_LEVEL', 10, 2),
('BUILDING', 'ACADEMY_BUILDING', 2, 'HERO_TRAIN_LEVEL', 10, 2),
('BUILDING', 'ACADEMY_BUILDING', 3, 'HERO_TRAIN_LEVEL', 10, 2),
('BUILDING', 'ACADEMY_BUILDING', 4, 'HERO_TRAIN_LEVEL', 10, 2),
('BUILDING', 'ACADEMY_BUILDING', 5, 'HERO_TRAIN_LEVEL', 10, 2),
('BUILDING', 'ACADEMY_BUILDING', 6, 'HERO_TRAIN_LEVEL', 10, 2),
('BUILDING', 'ACADEMY_BUILDING', 7, 'TRAINING_XP_BOOST', 25, 2),
('BUILDING', 'ACADEMY_BUILDING', 8, 'TRAINING_ASC_BOOST', 25, 2),
('BUILDING', 'ACADEMY_BUILDING', 9, 'TRAINING_XP_BOOST', 25, 2),
('BUILDING', 'ACADEMY_BUILDING', 10, 'TRAINING_ASC_BOOST', 25, 2);
delete from property_version where property_type = 'ACADEMY_BUILDING' and version = 2;
update property_version set active = false where property_type = 'ACADEMY_BUILDING';
insert into property_version (property_type, version, active) values ('ACADEMY_BUILDING', 2, true);

delete from dynamic_property where type = 'VIP_MAX_PLAYER';
insert into dynamic_property (category, type, level, value1, version) values
('PLAYER', 'VIP_MAX_PLAYER', 0, 100, 1),
('PLAYER', 'VIP_MAX_PLAYER', 1, 100, 1),
('PLAYER', 'VIP_MAX_PLAYER', 2, 200, 1),
('PLAYER', 'VIP_MAX_PLAYER', 3, 400, 1),
('PLAYER', 'VIP_MAX_PLAYER', 4, 800, 1),
('PLAYER', 'VIP_MAX_PLAYER', 5, 1600, 1),
('PLAYER', 'VIP_MAX_PLAYER', 6, 3200, 1),
('PLAYER', 'VIP_MAX_PLAYER', 7, 6400, 1),
('PLAYER', 'VIP_MAX_PLAYER', 8, 12800, 1),
('PLAYER', 'VIP_MAX_PLAYER', 9, 25600, 1),
('PLAYER', 'VIP_MAX_PLAYER', 10, 51200, 1),
('PLAYER', 'VIP_MAX_PLAYER', 11, 102400, 1),
('PLAYER', 'VIP_MAX_PLAYER', 12, 204800, 1),
('PLAYER', 'VIP_MAX_PLAYER', 13, 409600, 1),
('PLAYER', 'VIP_MAX_PLAYER', 14, 819200, 1),
('PLAYER', 'VIP_MAX_PLAYER', 15, 1638400, 1),
('PLAYER', 'VIP_MAX_PLAYER', 16, 3276800, 1),
('PLAYER', 'VIP_MAX_PLAYER', 17, 6553600, 1),
('PLAYER', 'VIP_MAX_PLAYER', 18, 13107200, 1),
('PLAYER', 'VIP_MAX_PLAYER', 19, 26214400, 1),
('PLAYER', 'VIP_MAX_PLAYER', 20, 52428800, 1);
delete from property_version where property_type = 'VIP_MAX_PLAYER' and version = 1;
insert into property_version (property_type, version, active) values ('VIP_MAX_PLAYER', 1, true);

delete from dynamic_property where type = 'VIP_LEVEL_REWARD_PLAYER';
insert into dynamic_property (category, type, level, resource_type, progress_stat, value1, version) values
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 1, null, 'EXPEDITION_SPEED', 5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 1, 'UNCOMMON_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 1, null, 'TRAINING_XP_BOOST', 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 1, null, 'TRAINING_ASC_BOOST', 10, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 2, null, 'MISSION_MAX_BATTLES', 1, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 2, 'UNCOMMON_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 2, 'RARE_GENOME', null, 20, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 2, null, 'MISSION_SPEED', 10, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 3, 'RESOURCE_GENERATION_SPEED', null, 5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 3, 'UNCOMMON_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 3, 'RARE_GENOME', null, 20, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 3, null, 'BARRACKS_SIZE', 5, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 4, null, 'UNCOMMON_GENOMES_NEEDED', -5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 4, 'UNCOMMON_GENOME', null, 120, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 4, 'RARE_GENOME', null, 30, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 4, null, 'UNCOMMON_INCUBATION_UP_PER_MIL', 1, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 5, null, 'BUILDER_SPEED', 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 5, 'UNCOMMON_GENOME', null, 120, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 5, 'RARE_GENOME', null, 30, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 5, null, 'GEAR_QUALITY_INCREASE', 5, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 6, null, 'EXPEDITION_SPEED', 5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 6, 'UNCOMMON_GENOME', null, 180, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 6, 'RARE_GENOME', null, 40, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 6, null, 'TRAINING_XP_BOOST', 15, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 6, null, 'TRAINING_ASC_BOOST', 15, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 7, null, 'MISSION_MAX_BATTLES', 1, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 7, 'UNCOMMON_GENOME', null, 180, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 7, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 7, null, 'MISSION_SPEED', 15, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 8, null, 'BUILDER_QUEUE', 1, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 8, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 8, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 8, null, 'BARRACKS_SIZE', 5, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 9, null, 'UNCOMMON_STARTING_LEVEL', 5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 9, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 9, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 9, null, 'COMMON_INCUBATION_UP_PER_MIL', 2, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 10, null, 'RARE_GENOMES_NEEDED', -5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 10, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 10, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 10, null, 'RARE_INCUBATION_UP_PER_MIL', 1, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 11, 'RESOURCE_GENERATION_SPEED', null, 5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 11, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 11, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 11, null, 'BUILDER_SPEED', 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 11, null, 'BARRACKS_SIZE', 5, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 12, null, 'EXPEDITION_SPEED', 5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 12, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 12, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 12, null, 'TRAINING_XP_BOOST', 20, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 12, null, 'TRAINING_ASC_BOOST', 20, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 13, null, 'MISSION_MAX_BATTLES', 2, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 13, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 13, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 13, null, 'MISSION_SPEED', 15, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 14, null, 'UNCOMMON_STARTING_LEVEL', 5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 14, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 14, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 14, null, 'EPIC_GENOMES_NEEDED', -5, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 15, null, 'BUILDER_SPEED', 15, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 15, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 15, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 15, null, 'GEAR_QUALITY_INCREASE', 5, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 16, null, 'TRAINING_XP_BOOST', 20, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 16, null, 'TRAINING_ASC_BOOST', 20, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 16, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 16, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 16, null, 'UNCOMMON_INCUBATION_UP_PER_MIL', 1, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 17, null, 'UNCOMMON_STARTING_LEVEL', 5, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 17, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 17, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 17, null, 'UNCOMMON_GENOMES_NEEDED', -5, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 18, null, 'MISSION_MAX_BATTLES', 2, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 18, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 18, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 18, null, 'RARE_GENOMES_NEEDED', -5, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 19, null, 'TRAINING_XP_BOOST', 25, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 19, null, 'TRAINING_ASC_BOOST', 25, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 19, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 19, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 19, null, 'RARE_INCUBATION_UP_PER_MIL', 1, 1),

('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 20, null, 'GEAR_QUALITY_INCREASE', 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 20, 'RARE_GENOME', null, 60, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 20, 'EPIC_GENOME', null, 10, 1),
('PLAYER', 'VIP_LEVEL_REWARD_PLAYER', 20, null, 'EPIC_GENOMES_NEEDED', -5, 1);

delete from property_version where property_type = 'VIP_LEVEL_REWARD_PLAYER' and version = 1;
insert into property_version (property_type, version, active) values ('VIP_LEVEL_REWARD_PLAYER', 1, true);
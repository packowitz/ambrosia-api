drop table if exists task_cluster;
create table task_cluster (
    id bigserial not null,
    name varchar(64) not null,
    category varchar(32) not null,
    sort_order int not null,
    primary key (id)
);

insert into task_cluster (id, name, category, sort_order) values
(1, 'Expedition Mastery', 'ACTIVITY', 1), -- expeditions done
(2, 'Busy Bee', 'ACTIVITY', 2), -- daily activity claimed
(3, 'Scrapper', 'ACTIVITY', 3), -- items breakdown

(4, 'Squanderer', 'RESOURCE_SPENT', 1), -- coins spent
(5, 'Steam Machine', 'RESOURCE_SPENT', 2), -- steam spent
(6, 'Screw it!', 'RESOURCE_SPENT', 3), -- cogwheels spent
(7, 'Metalhead', 'RESOURCE_SPENT', 4), -- metal spent
(8, 'Ironskull', 'RESOURCE_SPENT', 5), -- iron spent
(9, 'Beaver', 'RESOURCE_SPENT', 6), -- wood spent

(10, 'Architect', 'BUILDER', 1), -- all buildings level
(11, 'Jeweler', 'BUILDER', 2), -- jewels merged
(12, 'Pimp up my Ride', 'BUILDER', 3), -- vehicle part upgrades

(13, 'Novice Geneticist', 'LABORATORY', 1), -- simple incubations
(14, 'Scholar Geneticist', 'LABORATORY', 2), -- common incubations
(15, 'Expert Geneticist', 'LABORATORY', 3); -- uncommon incubations

ALTER SEQUENCE task_cluster_id_seq RESTART WITH 16;

drop table if exists task;
create table task (
    id bigserial not null,
    task_cluster_id bigint,
    number int not null,
    task_type varchar(32) not null,
    task_amount bigint not null,
    loot_box_id bigint not null,
    primary key (id)
);
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 1, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Expedition Mastery%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 2, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Busy Bee%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 3, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Scrapper%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 4, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Squanderer%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 5, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Steam Machine%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 6, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Screw it!%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 7, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Metalhead%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 8, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Ironskull%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 9, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Beaver%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 10, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Architect%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 11, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Jeweler%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 12, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Pimp up my Ride%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 13, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Novice Geneticist%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 14, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Scholar Geneticist%');
insert into task (task_cluster_id, number, task_type, task_amount, loot_box_id) (select 15, ROW_NUMBER () OVER (ORDER BY achievement_amount), achievement_type, achievement_amount, loot_box_id from achievement_reward where name like 'Expert Geneticist%');

drop table if exists player_task;
create table player_task (
    id bigserial not null,
    player_id bigint not null,
    task_cluster_id bigint not null,
    current_task_number int not null,
    primary key (id)
);
create index if not exists player_task_player_id_index on player_task (player_id);

insert into player_task (player_id, task_cluster_id, current_task_number)
(select distinct on (p.player_id, t.task_cluster_id) p.player_id, t.task_cluster_id, t.number
from player_achievement_reward p
    join achievement_reward r on p.reward_id = r.id
    join task t on r.achievement_type = t.task_type and r.achievement_amount = t.task_amount
order by p.player_id, t.task_cluster_id, t.number desc);


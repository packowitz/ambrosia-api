alter table loot_box add column if not exists type varchar(32) not null default 'LOOT';
update loot_box set type = 'ACHIEVEMENT' where name like 'Ach %';
update loot_box set type = 'EXPEDITION' where name like 'Exp %';
update loot_box set type = 'ODD_JOB' where name like 'Job %';
update loot_box set type = 'MERCHANT' where name like 'Mer %';
update loot_box set type = 'STORY' where id in (select distinct loot_box_id from story where loot_box_id is not null);

update loot_box set name = substr(name, 5) where name like 'Ach %';
update loot_box set name = substr(name, 5) where name like 'Exp %';
update loot_box set name = substr(name, 5) where name like 'Job %';
update loot_box set name = substr(name, 5) where name like 'Mer %';


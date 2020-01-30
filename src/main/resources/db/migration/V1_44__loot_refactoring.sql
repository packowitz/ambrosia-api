alter table gear_loot drop column rarity_names;
alter table gear_loot add if not exists legendary_chance int not null default 0;
alter table gear_loot add if not exists epic_chance int not null default 0;
alter table gear_loot add if not exists rare_chance int not null default 0;
alter table gear_loot add if not exists uncommon_chance int not null default 0;
alter table gear_loot add if not exists common_chance int not null default 0;

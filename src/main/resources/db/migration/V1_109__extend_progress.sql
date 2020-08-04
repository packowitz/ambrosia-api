alter table progress add column if not exists uncommon_incubation_super_up_per_mil int not null default 0;
alter table progress add column if not exists battle_xp_boost int not null default 0;
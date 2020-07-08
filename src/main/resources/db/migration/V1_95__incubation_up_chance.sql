alter table progress add column if not exists simple_incubation_up_per_mil int not null default 0;
alter table progress add column if not exists common_incubation_up_per_mil int not null default 0;
alter table progress add column if not exists uncommon_incubation_up_per_mil int not null default 0;
alter table progress add column if not exists rare_incubation_up_per_mil int not null default 0;
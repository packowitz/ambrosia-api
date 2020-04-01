alter table fight add column travel_duration int not null default 30;
alter table fight add column time_per_turn int not null default 1000;
alter table fight add column max_turns_per_stage int not null default 1000;
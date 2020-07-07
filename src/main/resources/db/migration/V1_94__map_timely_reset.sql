alter table player_map add column if not exists created timestamp not null default now();
alter table map add column if not exists reset_interval_hours int;
alter table map add column if not exists interval_from timestamp;
alter table map add column if not exists interval_to timestamp;
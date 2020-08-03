alter table upgrade drop column if exists seconds_spend;
alter table upgrade add column if not exists orig_duration bigint not null default 0;
update upgrade set orig_duration = round(EXTRACT(EPOCH FROM (finish_timestamp - start_timestamp)));
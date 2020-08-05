alter table odd_job_base add column if not exists name varchar(32) not null default 'Job name';
alter table odd_job add column if not exists name varchar(32) not null default 'Job name';
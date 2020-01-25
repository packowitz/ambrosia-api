alter table fight add if not exists resource_type varchar(30) not null default 'STEAM';
alter table fight add if not exists costs int not null default 5;
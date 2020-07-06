alter table dynamic_property add column if not exists version integer not null default 1;

create table if not exists property_version (
    id bigserial primary key,
    property_type varchar(32) not null,
    version integer not null,
    active boolean not null
);
insert into property_version (property_type, version, active) (select distinct type, 1, true from dynamic_property);
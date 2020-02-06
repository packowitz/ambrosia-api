insert into dynamic_property values (default, 'HERO', 'MERGE_ASC_HERO', 1, null, 110);
insert into dynamic_property values (default, 'HERO', 'MERGE_ASC_HERO', 2, null, 275);
insert into dynamic_property values (default, 'HERO', 'MERGE_ASC_HERO', 3, null, 520);
insert into dynamic_property values (default, 'HERO', 'MERGE_ASC_HERO', 4, null, 740);
insert into dynamic_property values (default, 'HERO', 'MERGE_ASC_HERO', 5, null, 935);
insert into dynamic_property values (default, 'HERO', 'MERGE_ASC_HERO', 6, null, 1220);

alter table dynamic_property add if not exists building_type varchar(30);

alter table building add if not exists wip_started timestamp with time zone;
alter table building add if not exists wip_finished timestamp with time zone;
alter table building add if not exists wip_ref bigint;
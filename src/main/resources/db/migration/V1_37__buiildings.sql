create table building (
    id bigserial,
    player_id bigint,
    type varchar(20) not null,
    level int not null default 1,
    upgrade_started timestamp with time zone,
    upgrade_finished timestamp with time zone,
    primary key (id)
);

create unique index building_player_type_uindex on building (player_id, type);

insert into building(player_id, type) (select id, 'ARMORY' from player);
insert into building(player_id, type) (select id, 'BARRACKS' from player);
insert into building(player_id, type) (select id, 'STORAGE_0' from player);

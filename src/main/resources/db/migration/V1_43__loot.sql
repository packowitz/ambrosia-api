create table gear_loot (
    id bigserial,
    name varchar(32) not null,
    set_names text not null,
    rarity_names text not null,
    type_names text not null,
    stat_from int not null,
    stat_to int not null,
    special_jewel_chance int,
    jewel4chance int,
    jewel3chance int,
    jewel2chance int,
    jewel1chance int,
    primary key (id)
);

create table loot_box (
    id bigserial,
    name varchar(32),
    primary key (id)
);

create table loot_item (
    id bigserial,
    loot_box_id bigint,
    slot_number int not null,
    item_order int not null,
    chance int not null,
    type varchar(16) not null,
    resource_type varchar(32),
    resource_amount int,
    hero_base_id bigint,
    gear_loot_id bigint,
    primary key (id)
);

alter table gear add if not exists stat_quality int not null default 50;
alter table map_tile add if not exists loot_box_id bigint;
alter table player_map_tile add if not exists chest_opened boolean not null default false;

insert into loot_box(id, name) values (1, 'Very few coins');
insert into loot_item(id, loot_box_id, slot_number, item_order, chance, type, resource_type, resource_amount) values
    (1, 1, 1, 1, 1000, 'RESOURCE', 'COINS', 5);

alter table fight add if not exists loot_box_id bigint not null default 1;


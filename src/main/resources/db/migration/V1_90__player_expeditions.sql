create index if not exists expedition_base_level_index on expedition_base (level);

create table if not exists player_expedition (
    id bigserial not null,
    player_id bigint not null,
    expedition_id bigint not null,
    vehicle_id bigint not null,
    hero1id bigint,
    hero2id bigint,
    hero3id bigint,
    hero4id bigint,
    completed boolean not null,
    name varchar(32) not null,
    description text not null,
    level int not null,
    rarity varchar(20) not null,
    start_timestamp timestamp not null,
    finish_timestamp timestamp not null,
    primary key (id)
);
create index if not exists player_expedition_player_id_index on player_expedition (player_id);

alter table vehicle add column if not exists player_expedition_id bigint;
alter table hero add column if not exists player_expedition_id bigint;
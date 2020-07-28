drop table if exists known_hero;
create table known_hero (
    id bigserial primary key,
    player_id bigint not null,
    hero_base_id bigint not null,
    created timestamp not null default now()
);
create index if not exists known_hero_player_id_index on known_hero (player_id);

insert into known_hero(player_id, hero_base_id) (select distinct player_id, hero_base_id from hero);
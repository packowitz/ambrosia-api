create table story_progress (
    id bigserial not null,
    player_id bigint not null,
    trigger varchar(32) not null,
    timestamp timestamp not null,
    primary key (id)
);
create index if not exists story_progress_player_id_index on story_progress (player_id);

alter table story add column loot_box_id bigint;

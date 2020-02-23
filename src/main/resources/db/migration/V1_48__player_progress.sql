create table progress (
    player_id bigint not null,
    garage_slots int not null default 1,
    offline_battle_speed int not null default 0,
    primary key (player_id)
);

insert into progress(player_id) (select id from player);

alter table vehicle add column if not exists slot int;

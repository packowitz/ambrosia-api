alter table progress add column max_offline_battles_per_mission int default 5;
update progress set offline_battle_speed = 100 where 1 = 1;

create table mission (
    id bigserial,
    player_id bigint not null,
    fight_id bigint not null,
    vehicle_id bigint not null,
    slot_number int not null,
    hero1id bigint,
    hero2id bigint,
    hero3id bigint,
    hero4id bigint,
    total_count int not null,
    won_count int not null,
    lost_count int not null,
    start_timestamp timestamp not null,
    finish_timestamp timestamp not null,
    primary key (id)
);

create table offline_battle (
    battle_id bigint not null,
    mission_id bigint,
    start_timestamp timestamp not null,
    finish_timestamp timestamp not null,
    battle_started boolean not null,
    battle_finished boolean not null,
    battle_won boolean not null,
    primary key (battle_id)
);

alter table vehicle add column mission_id bigint;
alter table hero add column mission_id bigint;

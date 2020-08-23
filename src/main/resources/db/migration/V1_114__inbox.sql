drop table if exists inbox_message;
create table inbox_message (
    id bigserial primary key,
    player_id bigint not null,
    message_type varchar(16) not null,
    sender_id bigint,
    read boolean not null default false,
    service_answer_needed boolean,
    send_timestamp timestamp not null default now(),
    valid_timestamp timestamp not null,
    message text not null
);
create index if not exists inbox_message_player_id_index on inbox_message (player_id);

drop table if exists inbox_message_item;
create table inbox_message_item (
    id bigserial primary key,
    message_id bigint,
    number integer not null,
    type varchar(16) not null,
    resource_type varchar(32),
    resource_amount integer,
    hero_base_id bigint,
    hero_level integer,
    jewel_type varchar(25),
    jewel_level integer,
    vehicle_base_id bigint,
    vehicle_part_type varchar(16),
    vehicle_part_quality varchar(16),
    progress_stat varchar(32),
    progress_bonus integer
);
create index if not exists inbox_message_item_message_id_index on inbox_message_item (message_id);


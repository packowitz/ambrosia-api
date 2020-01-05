alter table map add background varchar(25) not null default 'BLUE_SKY';
alter table map add last_modified timestamp with time zone default now() not null;
alter table player add color varchar(10);
alter table player add current_map_id bigint;

create table player_map (
  id bigserial,
  player_id bigint not null,
  map_id bigint,
  map_checked_timestamp timestamp with time zone not null,
  primary key (id),
  unique (player_id, map_id)
);

create table player_map_tile (
  id bigserial,
  player_map_id bigint,
  pos_x int not null,
  pos_y int not null,
  discoverable boolean not null default false,
  discovered boolean not null default false,
  victorious_fight boolean not null default false,
  primary key (id),
  unique (player_map_id, pos_x, pos_y)
);
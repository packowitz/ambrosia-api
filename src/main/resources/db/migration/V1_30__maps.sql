create table map (
  id bigserial,
  name varchar(30) not null,
  starting_map boolean not null default false,
  min_x int not null,
  max_x int not null,
  min_y int not null,
  max_y int not null,
  primary key (id)
);

create table map_tile (
  id bigserial,
  map_id bigint,
  pos_x int not null,
  pos_y int not null,
  type varchar(20) not null,
  red_always_revealed boolean not null default false,
  green_always_revealed boolean not null default false,
  blue_always_revealed boolean not null default false,
  structure varchar(25),
  portal_to_map_id bigint,
  dungeon_id bigint,
  fight_icon varchar(25),
  fight_repeatable boolean not null default false,
  primary key (id)
);
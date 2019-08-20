create table team (
  id bigserial,
  player_id bigint not null,
  type varchar(25) not null,
  hero1 bigint,
  hero2 bigint,
  hero3 bigint,
  hero4 bigint,
  primary key (id)
);

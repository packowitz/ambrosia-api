create table team (
  id bigserial,
  player_id bigint not null,
  type varchar(25) not null,
  hero1id bigint,
  hero2id bigint,
  hero3id bigint,
  hero4id bigint,
  primary key (id)
);

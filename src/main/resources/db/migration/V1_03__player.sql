create table player (
  id bigserial,
  name varchar(36) not null,
  email varchar(254) not null,
  password varchar(256) not null,
  admin boolean not null default false,
  xp int not null default 0,
  max_xp int not null default 100,
  level int not null default 1,
  primary key (id)
);

create unique index if not exists player_email_uidx on player(email);

create table dungeon (
    id bigserial,
    name varchar(30),
    service_account_id bigint,
    primary key (id)
);

create table dungeon_stage (
    id bigserial,
    dungeon_id bigint not null,
    stage int not null,
    hero1id bigint,
    hero2id bigint,
    hero3id bigint,
    hero4id bigint,
    primary key (id)
);

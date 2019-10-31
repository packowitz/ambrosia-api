create table battle_step_hero_state (
    id bigserial,
    battle_step_id bigint,
    position varchar(25) not null,
    status varchar(25) not null,
    hp_perc int not null,
    armor_perc int not null,
    speedbar_perc int not null,
    primary key (id)
);

create table battle_step_hero_state_buff (
    id bigserial,
    battle_step_hero_state_id bigint,
    buff varchar(25) not null,
    intensity int not null,
    duration int not null,
    primary key (id)
);

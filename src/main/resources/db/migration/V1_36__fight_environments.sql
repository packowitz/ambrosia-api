create table fight_environment (
    id bigserial,
    name varchar(30) not null,
    default_environment boolean not null default false,
    player_healing_dec int not null default 0,
    player_hot_blocked boolean not null default false,
    player_dot_dmg_inc int not null default 0,
    opp_dot_dmg_dec int not null default 0,
    player_green_dmg_inc int not null default 0,
    player_red_dmg_inc int not null default 0,
    player_blue_dmg_inc int not null default 0,
    opp_green_dmg_dec int not null default 0,
    opp_red_dmg_dec int not null default 0,
    opp_blue_dmg_dec int not null default 0,
    player_speed_bar_slowed int not null default 0,
    opp_speed_bar_fastened int not null default 0,
    primary key (id)
);

alter table fight add column environment_id bigint;

insert into fight_environment VALUES (1, 'Default', true);

update fight set environment_id = 1;

alter table fight alter column environment_id set not null;

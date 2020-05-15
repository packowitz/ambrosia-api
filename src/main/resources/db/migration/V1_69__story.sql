create table story (
    id bigserial not null,
    trigger varchar(32) not null,
    number integer not null,
    title varchar(64),
    message text not null,
    next_text varchar(20),
    left_pic varchar(20),
    right_pic varchar(20),
    primary key (id)
);

create table story_placeholder (
    id bigserial not null,
    identifier varchar(32) not null,
    red text not null,
    green text not null,
    blue text not null,
    primary key (id)
);

insert into story_placeholder (identifier, red, green, blue) VALUES ('#COLOR#', 'RED', 'GREEN', 'BLUE');

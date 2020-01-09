alter table map_tile rename column dungeon_id to fight_id;

alter table battle add map_id bigint;
alter table battle add map_pos_x int;
alter table battle add map_pos_y int;
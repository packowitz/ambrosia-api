alter table battle add previous_battle_id bigint;
alter table battle add dungeon_id bigint;
alter table battle add dungeon_stage int;

alter table battle_hero alter column player_id drop not null;

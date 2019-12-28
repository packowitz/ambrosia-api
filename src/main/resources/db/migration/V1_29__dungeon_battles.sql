alter table battle add previous_battle_id bigint;
alter table battle add next_battle_id bigint;
alter table battle add dungeon_id bigint;
alter table battle add dungeon_stage int;

alter table battle_hero alter column player_id drop not null;
alter table battle alter column opponent_id drop not null;

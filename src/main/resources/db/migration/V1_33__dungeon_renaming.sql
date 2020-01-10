alter table dungeon rename to fight;
alter table dungeon_stage rename column dungeon_id to fight_id;
alter table dungeon_stage rename to fight_stage;
alter table battle rename column dungeon_id to fight_id;
alter table battle rename column dungeon_stage to fight_stage;

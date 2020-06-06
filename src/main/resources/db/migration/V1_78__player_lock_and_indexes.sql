alter table player add column if not exists locked timestamp;

create index if not exists battle_player_id_index on battle (player_id);
create index if not exists battle_hero_buff_battle_hero_id_index on battle_hero_buff (battle_hero_id);
create index if not exists battle_step_battle_id_index on battle_step (battle_id);
create index if not exists battle_step_action_battle_step_id_index on battle_step_action (battle_step_id);
create index if not exists battle_step_hero_state_battle_step_id_index on battle_step_hero_state (battle_step_id);
create index if not exists battle_step_hero_state_buff_battle_step_hero_state_id_index on battle_step_hero_state_buff (battle_step_hero_state_id);
create index if not exists building_player_id_index on building (player_id);
create index if not exists gear_player_id_index on gear (player_id);
create index if not exists hero_player_id_index on hero (player_id);
create index if not exists hero_skill_hero_id_index on hero_skill (hero_id);
create index if not exists hero_skill_action_skill_id_index on hero_skill_action (skill_id);
create index if not exists hero_skill_level_skill_id_index on hero_skill_level (skill_id);
create index if not exists incubator_player_id_index on incubator (player_id);
create index if not exists jewelry_player_id_index on jewelry (player_id);
create index if not exists loot_item_loot_box_id_index on loot_item (loot_box_id);
create index if not exists map_tile_map_id_index on map_tile (map_id);
create index if not exists mission_player_id_index on mission (player_id);
create index if not exists offline_battle_mission_id_index on offline_battle (mission_id);
create index if not exists player_map_player_id_index on player_map (player_id);
create index if not exists player_map_tile_player_map_id_index on player_map_tile (player_map_id);
create index if not exists team_player_id_index on team (player_id);
create index if not exists upgrade_player_id_index on upgrade (player_id);
create index if not exists vehicle_player_id_index on vehicle (player_id);
create index if not exists vehicle_part_player_id_index on vehicle_part (player_id);
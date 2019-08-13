alter table hero_skill_action alter column trigger_value drop not null;

alter table hero_skill_action add trigger_chance int default 100 not null;

alter table hero_skill_action add effect_duration int;

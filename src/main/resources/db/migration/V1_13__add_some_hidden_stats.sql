alter table battle_hero add hero_buff_duration_inc int default 0 not null;
alter table battle_hero add hero_debuff_duration_inc int default 0 not null;
alter table battle_hero add hero_heal_per_turn int default 0 not null;
alter table battle_hero add hero_dmg_per_turn int default 0 not null;

insert into dynamic_property values (default, 'BUFF', 'DOT_BUFF', 1, 'DMG_PER_TURN', 1);
insert into dynamic_property values (default, 'BUFF', 'DOT_BUFF', 2, 'DMG_PER_TURN', 3);
insert into dynamic_property values (default, 'BUFF', 'DOT_BUFF', 3, 'DMG_PER_TURN', 5);
insert into dynamic_property values (default, 'BUFF', 'DOT_BUFF', 4, 'DMG_PER_TURN', 8);
insert into dynamic_property values (default, 'BUFF', 'DOT_BUFF', 5, 'DMG_PER_TURN', 12);
insert into dynamic_property values (default, 'BUFF', 'HOT_BUFF', 1, 'HEAL_PER_TURN', 1);
insert into dynamic_property values (default, 'BUFF', 'HOT_BUFF', 2, 'HEAL_PER_TURN', 3);
insert into dynamic_property values (default, 'BUFF', 'HOT_BUFF', 3, 'HEAL_PER_TURN', 5);
insert into dynamic_property values (default, 'BUFF', 'HOT_BUFF', 4, 'HEAL_PER_TURN', 8);
insert into dynamic_property values (default, 'BUFF', 'HOT_BUFF', 5, 'HEAL_PER_TURN', 12);

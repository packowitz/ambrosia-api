update gear set type = 'GLOVES' where type = 'PANTS';
alter table hero rename column pants_id to gloves_id;
update dynamic_property set type = 'GLOVES_GEAR' where type = 'PANTS_GEAR';
alter table map_tile rename column red_always_revealed to always_revealed;
alter table map_tile drop column green_always_revealed;
alter table map_tile drop column blue_always_revealed;
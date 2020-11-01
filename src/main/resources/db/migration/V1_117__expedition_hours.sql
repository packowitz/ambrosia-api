alter table expedition_base rename column duration_minutes to duration_hours;
update expedition_base set duration_hours = duration_hours / 60;
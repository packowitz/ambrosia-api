create table if not exists expedition (
    id bigserial not null,
    expedition_base_id bigint,
    active boolean not null,
    created timestamp not null,
    available_until timestamp not null,
    primary key (id)
);

update expedition_base set rarity = 'SIMPLE' where rarity = '0';
update expedition_base set rarity = 'COMMON' where rarity = '1';
update expedition_base set rarity = 'UNCOMMON' where rarity = '2';
update expedition_base set rarity = 'RARE' where rarity = '3';
update expedition_base set rarity = 'EPIC' where rarity = '4';
update expedition_base set rarity = 'LEGENDARY' where rarity = '5';

update vehicle_base set special_part1 = 'ENGINE' where special_part1 = '0';
update vehicle_base set special_part1 = 'FRAME' where special_part1 = '1';
update vehicle_base set special_part1 = 'COMPUTER' where special_part1 = '2';
update vehicle_base set special_part1 = 'SMOKE_BOMB' where special_part1 = '3';
update vehicle_base set special_part1 = 'RAILGUN' where special_part1 = '4';
update vehicle_base set special_part1 = 'SNIPER_SCOPE' where special_part1 = '5';
update vehicle_base set special_part1 = 'MEDI_KIT' where special_part1 = '6';
update vehicle_base set special_part1 = 'REPAIR_KIT' where special_part1 = '7';
update vehicle_base set special_part1 = 'EXTRA_ARMOR' where special_part1 = '8';
update vehicle_base set special_part1 = 'NIGHT_VISION' where special_part1 = '9';
update vehicle_base set special_part1 = 'MAGNETIC_SHIELD' where special_part1 = '10';
update vehicle_base set special_part1 = 'MISSILE_DEFENSE' where special_part1 = '11';
update vehicle_base set special_part2 = 'ENGINE' where special_part2 = '0';
update vehicle_base set special_part2 = 'FRAME' where special_part2 = '1';
update vehicle_base set special_part2 = 'COMPUTER' where special_part2 = '2';
update vehicle_base set special_part2 = 'SMOKE_BOMB' where special_part2 = '3';
update vehicle_base set special_part2 = 'RAILGUN' where special_part2 = '4';
update vehicle_base set special_part2 = 'SNIPER_SCOPE' where special_part2 = '5';
update vehicle_base set special_part2 = 'MEDI_KIT' where special_part2 = '6';
update vehicle_base set special_part2 = 'REPAIR_KIT' where special_part2 = '7';
update vehicle_base set special_part2 = 'EXTRA_ARMOR' where special_part2 = '8';
update vehicle_base set special_part2 = 'NIGHT_VISION' where special_part2 = '9';
update vehicle_base set special_part2 = 'MAGNETIC_SHIELD' where special_part2 = '10';
update vehicle_base set special_part2 = 'MISSILE_DEFENSE' where special_part2 = '11';
update vehicle_base set special_part3 = 'ENGINE' where special_part3 = '0';
update vehicle_base set special_part3 = 'FRAME' where special_part3 = '1';
update vehicle_base set special_part3 = 'COMPUTER' where special_part3 = '2';
update vehicle_base set special_part3 = 'SMOKE_BOMB' where special_part3 = '3';
update vehicle_base set special_part3 = 'RAILGUN' where special_part3 = '4';
update vehicle_base set special_part3 = 'SNIPER_SCOPE' where special_part3 = '5';
update vehicle_base set special_part3 = 'MEDI_KIT' where special_part3 = '6';
update vehicle_base set special_part3 = 'REPAIR_KIT' where special_part3 = '7';
update vehicle_base set special_part3 = 'EXTRA_ARMOR' where special_part3 = '8';
update vehicle_base set special_part3 = 'NIGHT_VISION' where special_part3 = '9';
update vehicle_base set special_part3 = 'MAGNETIC_SHIELD' where special_part3 = '10';
update vehicle_base set special_part3 = 'MISSILE_DEFENSE' where special_part3 = '11';

update vehicle_base set engine_quality = 'BASIC' where engine_quality = '0';
update vehicle_base set engine_quality = 'MODERATE' where engine_quality = '1';
update vehicle_base set engine_quality = 'GOOD' where engine_quality = '2';
update vehicle_base set frame_quality = 'BASIC' where frame_quality = '0';
update vehicle_base set frame_quality = 'MODERATE' where frame_quality = '1';
update vehicle_base set frame_quality = 'GOOD' where frame_quality = '2';
update vehicle_base set computer_quality = 'BASIC' where computer_quality = '0';
update vehicle_base set computer_quality = 'MODERATE' where computer_quality = '1';
update vehicle_base set computer_quality = 'GOOD' where computer_quality = '2';
update vehicle_base set special_part1quality = 'BASIC' where special_part1quality = '0';
update vehicle_base set special_part1quality = 'MODERATE' where special_part1quality = '1';
update vehicle_base set special_part1quality = 'GOOD' where special_part1quality = '2';
update vehicle_base set special_part2quality = 'BASIC' where special_part2quality = '0';
update vehicle_base set special_part2quality = 'MODERATE' where special_part2quality = '1';
update vehicle_base set special_part2quality = 'GOOD' where special_part2quality = '2';
update vehicle_base set special_part3quality = 'BASIC' where special_part3quality = '0';
update vehicle_base set special_part3quality = 'MODERATE' where special_part3quality = '1';
update vehicle_base set special_part3quality = 'GOOD' where special_part3quality = '2';

update vehicle_part set type = 'ENGINE' where type = '0';
update vehicle_part set type = 'FRAME' where type = '1';
update vehicle_part set type = 'COMPUTER' where type = '2';
update vehicle_part set type = 'SMOKE_BOMB' where type = '3';
update vehicle_part set type = 'RAILGUN' where type = '4';
update vehicle_part set type = 'SNIPER_SCOPE' where type = '5';
update vehicle_part set type = 'MEDI_KIT' where type = '6';
update vehicle_part set type = 'REPAIR_KIT' where type = '7';
update vehicle_part set type = 'EXTRA_ARMOR' where type = '8';
update vehicle_part set type = 'NIGHT_VISION' where type = '9';
update vehicle_part set type = 'MAGNETIC_SHIELD' where type = '10';
update vehicle_part set type = 'MISSILE_DEFENSE' where type = '11';
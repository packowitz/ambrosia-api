delete from dynamic_property where type = 'STORAGE_RESOURCES';
delete from dynamic_property where type = 'PLAYER_LVL_RESOURCES' and resource_type like '%_MAX';

insert into dynamic_property (category, type, level, resource_type, value1) values 
  ('BUILDING', 'STORAGE_BUILDING', 1, 'STEAM_MAX', 30),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'STEAM_MAX', 5),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'COGWHEELS_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'COGWHEELS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'COGWHEELS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'COGWHEELS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'COGWHEELS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'COGWHEELS_MAX', 1),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'TOKENS_MAX', 4),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'TOKENS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'TOKENS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'TOKENS_MAX', 1),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'PREMIUM_STEAM_MAX', 20),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'PREMIUM_STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'PREMIUM_STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'PREMIUM_STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'PREMIUM_STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'PREMIUM_STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'PREMIUM_STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'PREMIUM_STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'PREMIUM_STEAM_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'PREMIUM_STEAM_MAX', 5),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'PREMIUM_COGWHEELS_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'PREMIUM_COGWHEELS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'PREMIUM_COGWHEELS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'PREMIUM_COGWHEELS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'PREMIUM_COGWHEELS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'PREMIUM_COGWHEELS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'PREMIUM_COGWHEELS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'PREMIUM_COGWHEELS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'PREMIUM_COGWHEELS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'PREMIUM_COGWHEELS_MAX', 2),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'PREMIUM_TOKENS_MAX', 5),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'PREMIUM_TOKENS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'PREMIUM_TOKENS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'PREMIUM_TOKENS_MAX', 1),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'PREMIUM_TOKENS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'PREMIUM_TOKENS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'PREMIUM_TOKENS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'PREMIUM_TOKENS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'PREMIUM_TOKENS_MAX', 2),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'PREMIUM_TOKENS_MAX', 2),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'METAL_MAX', 150),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'METAL_MAX', 350),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'METAL_MAX', 500),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'METAL_MAX', 1000),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'METAL_MAX', 1500),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'METAL_MAX', 2000),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'METAL_MAX', 2500),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'METAL_MAX', 3000),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'METAL_MAX', 3500),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'METAL_MAX', 4000),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'IRON_MAX', 50),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'IRON_MAX', 150),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'IRON_MAX', 300),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'IRON_MAX', 500),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'IRON_MAX', 700),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'IRON_MAX', 800),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'IRON_MAX', 1000),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'IRON_MAX', 1500),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'IRON_MAX', 2000),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'IRON_MAX', 3000),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'STEAL_MAX', 20),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'STEAL_MAX', 80),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'STEAL_MAX', 100),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'STEAL_MAX', 200),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'STEAL_MAX', 600),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'STEAL_MAX', 500),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'STEAL_MAX', 1000),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'STEAL_MAX', 1500),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'STEAL_MAX', 2000),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'STEAL_MAX', 4000),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'WOOD_MAX', 150),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'WOOD_MAX', 350),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'WOOD_MAX', 500),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'WOOD_MAX', 1000),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'WOOD_MAX', 1500),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'WOOD_MAX', 2000),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'WOOD_MAX', 2500),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'WOOD_MAX', 3000),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'WOOD_MAX', 3500),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'WOOD_MAX', 4000),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'BROWN_COAL_MAX', 50),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'BROWN_COAL_MAX', 150),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'BROWN_COAL_MAX', 300),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'BROWN_COAL_MAX', 500),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'BROWN_COAL_MAX', 700),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'BROWN_COAL_MAX', 800),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'BROWN_COAL_MAX', 1000),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'BROWN_COAL_MAX', 1500),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'BROWN_COAL_MAX', 2000),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'BROWN_COAL_MAX', 3000),

  ('BUILDING', 'STORAGE_BUILDING', 1, 'BLACK_COAL_MAX', 20),
  ('BUILDING', 'STORAGE_BUILDING', 2, 'BLACK_COAL_MAX', 80),
  ('BUILDING', 'STORAGE_BUILDING', 3, 'BLACK_COAL_MAX', 100),
  ('BUILDING', 'STORAGE_BUILDING', 4, 'BLACK_COAL_MAX', 200),
  ('BUILDING', 'STORAGE_BUILDING', 5, 'BLACK_COAL_MAX', 600),
  ('BUILDING', 'STORAGE_BUILDING', 6, 'BLACK_COAL_MAX', 500),
  ('BUILDING', 'STORAGE_BUILDING', 7, 'BLACK_COAL_MAX', 1000),
  ('BUILDING', 'STORAGE_BUILDING', 8, 'BLACK_COAL_MAX', 1500),
  ('BUILDING', 'STORAGE_BUILDING', 9, 'BLACK_COAL_MAX', 2000),
  ('BUILDING', 'STORAGE_BUILDING', 10, 'BLACK_COAL_MAX', 4000);

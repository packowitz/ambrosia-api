alter table builder_item rename to upgrade;
alter table upgrade add column seconds_spend int default 0;

update resources set metal_max = 150, iron_max = 50, steal_max = 20, wood_max = 150, brown_coal_max = 50, black_coal_max = 20 where 1=1;
update dynamic_property set value1 = 150 where type = 'STORAGE_RESOURCES' and level = 1 and resource_type = 'METAL_MAX';
update dynamic_property set value1 = 50 where type = 'STORAGE_RESOURCES' and level = 1 and resource_type = 'IRON_MAX';
update dynamic_property set value1 = 20 where type = 'STORAGE_RESOURCES' and level = 1 and resource_type = 'STEAL_MAX';
update dynamic_property set value1 = 150 where type = 'STORAGE_RESOURCES' and level = 1 and resource_type = 'WOOD_MAX';
update dynamic_property set value1 = 50 where type = 'STORAGE_RESOURCES' and level = 1 and resource_type = 'BROWN_COAL_MAX';
update dynamic_property set value1 = 20 where type = 'STORAGE_RESOURCES' and level = 1 and resource_type = 'BLACK_COAL_MAX';
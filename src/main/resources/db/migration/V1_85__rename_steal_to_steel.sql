alter table resources rename column steal to steel;
alter table resources rename column steal_max to steel_max;
alter table resources rename column steal_used to steel_used;

update dynamic_property set resource_type = 'STEEL' where resource_type = 'STEAL';
update dynamic_property set resource_type = 'STEEL_MAX' where resource_type = 'STEAL_MAX';
alter table merchant_item add column amount integer not null default 1;
alter table merchant_player_item add column amount_available integer not null default 1;
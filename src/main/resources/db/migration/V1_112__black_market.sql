create table if not exists black_market_item (
    id bigserial primary key,
    active boolean not null,
    sort_order integer not null,
    loot_box_id bigserial not null,
    price_type varchar(32) not null,
    price_amount int not null
);


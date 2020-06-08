create table audit_log (
    id bigserial not null,
    player_id bigint not null,
    action text not null,
    error boolean not null default false,
    admin_action boolean not null default false,
    beta_tester_action boolean not null default false,
    timestamp timestamp not null,
    primary key (id)
);
create index if not exists audit_log_player_id_index on audit_log (player_id);

alter table player add column last_action timestamp;
alter table player add column last_login timestamp;

drop index if exists player_email_uidx;
create unique index if not exists player_email_uidx on player(email) where service_account = false;
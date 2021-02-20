------------------------------------------------------------------------------------------------------------------------
create sequence if not exists players_seq;
create sequence if not exists wallets_seq;
------------------------------------------------------------------------------------------------------------------------
create table if not exists players (
    id  integer default players_seq.nextval primary key,
    first_name varchar(250) not null,
    last_name varchar(250) not null
);
------------------------------------------------------------------------------------------------------------------------
create table if not exists wallets (
    id  integer default wallets_seq.nextval primary key,
    player_id  integer not null,
    balance numeric(20,2) not null,
    foreign key (player_id) references players (id) on delete cascade
);
------------------------------------------------------------------------------------------------------------------------
create table if not exists transactions (
    id varchar(250) primary key,
    wallet_id  integer not null,
    amount numeric(20,2) not null,
    date timestamp not null,
    foreign key (wallet_id) references wallets (id) on delete cascade
);
------------------------------------------------------------------------------------------------------------------------
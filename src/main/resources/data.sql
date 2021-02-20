------------------------------------------------------------------------------------------------------------------------
insert into players (id, first_name, last_name)
select * from (
    select players_seq.nextval id, 'Angus', 'Young' union
    select players_seq.nextval id, 'Elon', 'Musk' union
    select players_seq.nextval id, 'Nikola', 'Tesla'
) x where not exists(select true from players) order by id;
------------------------------------------------------------------------------------------------------------------------
insert into wallets (player_id, balance)
select * from (
    select 1, 100 union
    select 2, 200 union
    select 3, 300
) x where not exists(select true from wallets);
------------------------------------------------------------------------------------------------------------------------
insert into transactions (id, wallet_id, amount, date)
select * from (
    select '34df047f-0500-4933-9718-374f62dc32b8', 1, 50, '2021-02-01 10:00:00.000000' union
    select '2417a710-27b1-4cf0-9ecb-34c81c5df715', 1, 50, '2021-02-01 11:00:00.000000' union
    select '6f22eeb1-87c7-4d81-ad9d-477a4166657e', 2, 100, '2021-02-01 12:00:00.000000' union
    select '84dce3e4-e19f-4afc-b079-eb48795b71b6', 2, 100, '2021-02-01 13:00:00.000000' union
    select '752fa23a-9609-4731-9d23-23f37e520ca0', 3, 150, '2021-02-01 14:00:00.000000' union
    select '2e2301ae-ad87-45fb-a31b-df00ec692013', 3, 150, '2021-02-01 15:00:00.000000'
) x where not exists(select true from transactions);
------------------------------------------------------------------------------------------------------------------------

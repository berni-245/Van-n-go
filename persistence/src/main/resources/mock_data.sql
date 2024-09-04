-- Zones
insert into country
values (1, 'Argentina', 'AR')
on conflict do nothing;
insert into province
values (1, 'Capital Federal')
on conflict do nothing;
insert into province
values (2, 'Buenos Aires')
on conflict do nothing;
insert into neighborhood
values (1, 'Palermo')
on conflict do nothing;
insert into neighborhood
values (2, 'Belgrano')
on conflict do nothing;
insert into neighborhood
values (3, 'Adrogué')
on conflict do nothing;

insert into zone
values (1, 1, 1, 1)
on conflict do nothing;
insert into zone
values (2, 1, 1, 2)
on conflict do nothing;
insert into zone
values (3, 1, 2, 3)
on conflict do nothing;

-- Users
insert into app_user
values (1, 'franco', 'fmorroni@itba.edu.ar', '1234')
on conflict do nothing;
insert into app_user
values (2, 'berna', 'bzapico@itba.edu.ar', '1234')
on conflict do nothing;

insert into driver
values (1, 'lalala')
on conflict do nothing;
insert into driver
values (2, 'lololo')
on conflict do nothing;
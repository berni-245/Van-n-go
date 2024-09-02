-- Zones
insert into country
values (1, 'Argentina', 'AR');
insert into province
values (1, 'Capital Federal');
insert into province
values (2, 'Buenos Aires');
insert into neighborhood
values (1, 'Palermo');
insert into neighborhood
values (2, 'Belgrano');
insert into neighborhood
values (3, 'Adrogué');

insert into country_province
values (1, 1);
insert into country_province
values (1, 2);
insert into province_neighborhood
values (1, 1);
insert into province_neighborhood
values (1, 2);
insert into province_neighborhood
values (2, 3);

-- Users
insert into app_user
values (1, 'franco', 'fmorroni@itba.edu.ar', '1234');
insert into app_user
values (2, 'berna', 'bzapico@itba.edu.ar', '1234');

insert into driver
values (1, 'lalala');
insert into driver
values (2, 'lololo');

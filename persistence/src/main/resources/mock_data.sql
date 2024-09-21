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
insert into neighborhood
values (4, 'Agronomía')
on conflict do nothing;
insert into neighborhood
values (5, 'Almagro')
on conflict do nothing;
insert into neighborhood
values (6, 'Balvanera')
on conflict do nothing;
insert into neighborhood
values (7, 'Barracas')
on conflict do nothing;
insert into neighborhood
values (8, 'Boedo')
on conflict do nothing;
insert into neighborhood
values (9, 'Caballito')
on conflict do nothing;
insert into neighborhood
values (10, 'Chacarita')
on conflict do nothing;
insert into neighborhood
values (11, 'Coghlan')
on conflict do nothing;
insert into neighborhood
values (12, 'Colegiales')
on conflict do nothing;
insert into neighborhood
values (13, 'Constitución')
on conflict do nothing;
insert into neighborhood
values (14, 'Flores')
on conflict do nothing;
insert into neighborhood
values (15, 'Floresta')
on conflict do nothing;
insert into neighborhood
values (16, 'La Boca')
on conflict do nothing;
insert into neighborhood
values (17, 'La Paternal')
on conflict do nothing;
insert into neighborhood
values (18, 'Liniers')
on conflict do nothing;
insert into neighborhood
values (19, 'Mataderos')
on conflict do nothing;
insert into neighborhood
values (20, 'Monte Castro')
on conflict do nothing;
insert into neighborhood
values (21, 'Montserrat')
on conflict do nothing;
insert into neighborhood
values (22, 'Nueva Pompeya')
on conflict do nothing;
insert into neighborhood
values (23, 'Nuñez')
on conflict do nothing;
insert into neighborhood
values (24, 'Parque Avellaneda')
on conflict do nothing;
insert into neighborhood
values (25, 'Parque Chacabuco')
on conflict do nothing;
insert into neighborhood
values (26, 'Parque Chas')
on conflict do nothing;
insert into neighborhood
values (27, 'Parque Patricios')
on conflict do nothing;
insert into neighborhood
values (28, 'Puerto Madero')
on conflict do nothing;
insert into neighborhood
values (29, 'Recoleta')
on conflict do nothing;
insert into neighborhood
values (30, 'Retiro')
on conflict do nothing;
insert into neighborhood
values (31, 'Saavedra')
on conflict do nothing;
insert into neighborhood
values (32, 'San Cristóbal')
on conflict do nothing;
insert into neighborhood
values (33, 'San Nicolás')
on conflict do nothing;
insert into neighborhood
values (34, 'San Telmo')
on conflict do nothing;
insert into neighborhood
values (35, 'Versalles')
on conflict do nothing;
insert into neighborhood
values (36, 'Villa Crespo')
on conflict do nothing;
insert into neighborhood
values (37, 'Villa Devoto')
on conflict do nothing;
insert into neighborhood
values (38, 'Villa General Mitre')
on conflict do nothing;
insert into neighborhood
values (39, 'Villa Lugano')
on conflict do nothing;
insert into neighborhood
values (40, 'Villa Luro')
on conflict do nothing;
insert into neighborhood
values (41, 'Villa Ortúzar')
on conflict do nothing;
insert into neighborhood
values (42, 'Villa Pueyrredón')
on conflict do nothing;
insert into neighborhood
values (43, 'Villa Real')
on conflict do nothing;
insert into neighborhood
values (44, 'Villa Riachuelo')
on conflict do nothing;
insert into neighborhood
values (45, 'Villa Santa Rita')
on conflict do nothing;
insert into neighborhood
values (46, 'Villa Soldati')
on conflict do nothing;
insert into neighborhood
values (47, 'Villa Urquiza')
on conflict do nothing;
insert into neighborhood
values (48, 'Villa del Parque')
on conflict do nothing;
insert into neighborhood
values (49, 'Vélez Sarsfield')
on conflict do nothing;

insert into zone (country_id, province_id, neighborhood_id)
values (1, 1, 1)
on conflict do nothing;
insert into zone (country_id, province_id, neighborhood_id)
values (1, 1, 2)
on conflict do nothing;
insert into zone (country_id, province_id, neighborhood_id)
values (1, 2, 3)
on conflict do nothing;
insert into zone (country_id, province_id, neighborhood_id)
select 1, 1, id 
from neighborhood
where id > 3 and id <= 49;

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
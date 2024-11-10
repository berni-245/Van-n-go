-- Zones
insert into country
values (1, 'Argentina', 'AR')
on conflict do nothing;

insert into province
values (1, 'Capital Federal'),
       (2, 'Buenos Aires')
on conflict do nothing;

insert into neighborhood
values (1, 'Palermo'),
       (2, 'Belgrano'),
       (3, 'Adrogué'),
       (4, 'Agronomía'),
       (5, 'Almagro'),
       (6, 'Balvanera'),
       (7, 'Barracas'),
       (8, 'Boedo'),
       (9, 'Caballito'),
       (10, 'Chacarita'),
       (11, 'Coghlan'),
       (12, 'Colegiales'),
       (13, 'Constitución'),
       (14, 'Flores'),
       (15, 'Floresta'),
       (16, 'La Boca'),
       (17, 'La Paternal'),
       (18, 'Liniers'),
       (19, 'Mataderos'),
       (20, 'Monte Castro'),
       (21, 'Montserrat'),
       (22, 'Nueva Pompeya'),
       (23, 'Nuñez'),
       (24, 'Parque Avellaneda'),
       (25, 'Parque Chacabuco'),
       (26, 'Parque Chas'),
       (27, 'Parque Patricios'),
       (28, 'Puerto Madero'),
       (29, 'Recoleta'),
       (30, 'Retiro'),
       (31, 'Saavedra'),
       (32, 'San Cristóbal'),
       (33, 'San Nicolás'),
       (34, 'San Telmo'),
       (35, 'Versalles'),
       (36, 'Villa Crespo'),
       (37, 'Villa Devoto'),
       (38, 'Villa General Mitre'),
       (39, 'Villa Lugano'),
       (40, 'Villa Luro'),
       (41, 'Villa Ortúzar'),
       (42, 'Villa Pueyrredón'),
       (43, 'Villa Real'),
       (44, 'Villa Riachuelo'),
       (45, 'Villa Santa Rita'),
       (46, 'Villa Soldati'),
       (47, 'Villa Urquiza'),
       (48, 'Villa del Parque'),
       (49, 'Vélez Sarsfield')
on conflict do nothing;

insert into zone (country_id, province_id, neighborhood_id)
values (1, 2, 3),
       (1, 1, 1),
       (1, 1, 2)
on conflict do nothing;
insert into zone (country_id, province_id, neighborhood_id)
select 1, 1, id
from neighborhood
where id > 3
  and id <= 49;
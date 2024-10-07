insert into app_user (id, username, mail, password) values
    (1, 'JuanClient', 'juanC@mail.com', '123321'),
    (2, 'AnotherJuanClient', 'AjuanC@mail.com', '123321'),
    (3, 'JuanDriver', 'juanD@mail.com', '123321');


insert into client (user_id) values (1), (2);
insert into driver (user_id) values (3);

insert into vehicle (id, driver_id) values (1, 3);

-- JuanDriver trabaja en zona de id 1 de 0-4am los miércoles, jueves y viernes en su vehículo de id 1
insert into weekly_availability (id, week_day, hour_block_id, zone_id, vehicle_id) values
    -- miércoles
    (1, 3, 1, 1, 1),
    (2, 3, 2, 1, 1),
    (3, 3, 3, 1, 1),
    (4, 3, 4, 1, 1),
    -- jueves
    (5, 4, 1, 1, 1),
    (6, 4, 2, 1, 1),
    (7, 4, 3, 1, 1),
    (8, 4, 4, 1, 1),
    -- viernes
    (9, 5, 1, 1, 1),
    (10, 5, 2, 1, 1),
    (11, 5, 3, 1, 1),
    (12, 5, 4, 1, 1)

;

insert into booking (id, date, hour_start_id, hour_end_id, client_id, vehicle_id, zone_id, state) values
    (500, '2030-5-2', 2, 3, 1, 1, 1, 'ACCEPTED'), -- will be with booking id 500
    (501, '2030-5-3', 2, 3, 1, 1, 1, 'PENDING'),  -- will be with booking id 501
    (502, '2030-5-3', 2, 3, 1, 1, 1, 'PENDING');  -- will be with booking id 502
insert into app_user (id, username, mail, password, language) values
    (1, 'JuanClient', 'juanC@mail.com', '123321', 'SPANISH'),
    (2, 'AnotherJuanClient', 'AjuanC@mail.com', '123321', 'SPANISH'),
    (3, 'JuanDriver', 'juanD@mail.com', '123321', 'SPANISH');


insert into client (id, zone_id) values (1, 1), (2, 1);
insert into driver (id) values (3);

insert into vehicle (id, driver_id, plate_number, volume_m3, description, hourly_rate)
    values (1, 3, 'AAA999', 100, 'hola', 100);

-- JuanDriver trabaja en zona de id 1 a la noche los miércoles, jueves y viernes en su vehículo de id 1
insert into vehicle_zone (vehicle_id, zone_id) values (1, 1);

insert into vehicle_availability (id, week_day, vehicle_id, shift_period) values
    -- miércoles
    (1, 'WEDNESDAY', 1, 'EVENING'),
    -- jueves
    (2, 'THURSDAY', 1, 'AFTERNOON'),
    (3, 'THURSDAY', 1, 'EVENING'),
    -- viernes
    (4, 'FRIDAY', 1, 'MORNING'),
    (5, 'FRIDAY', 1, 'AFTERNOON'),
    (6, 'FRIDAY', 1, 'EVENING')

;

insert into booking (id, date, shift_period, client_id, vehicle_id, origin_zone_id, destination_zone_id, state) values
    (500, '2030-5-2', 'EVENING', 2, 1, 1, 20, 'ACCEPTED'), -- will be with booking id 500
    (501, '2030-5-3', 'AFTERNOON', 1, 1, 1, 20, 'PENDING'),  -- will be with booking id 501
    (502, '2030-5-3', 'AFTERNOON', 2, 1, 1, 20, 'PENDING'),  -- will be with booking id 502
    (503, '2030-5-3', 'EVENING', 1, 1, 1, 20, 'PENDING');  -- will be with booking id 503
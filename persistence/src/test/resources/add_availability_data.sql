insert into app_user (id, username, mail, password, language, creation_time) values
    (1, 'JuanClient', 'juanC@mail.com', '123321', 'SPANISH', '2024-11-7 12:00:00');

insert into driver (id) values (1);

insert into vehicle (id, driver_id, plate_number, volume_m3, description, hourly_rate)
    values (1, 1, 'AAA999', 100, 'hola', 100);

insert into vehicle_zone (vehicle_id, zone_id) values (1, 1);

insert into vehicle_availability (id, week_day, vehicle_id, shift_period) values
-- viernes
    (10, 'FRIDAY', 1, 'MORNING'),
    (11, 'FRIDAY', 1, 'AFTERNOON'),
    (12, 'FRIDAY', 1, 'EVENING')
;
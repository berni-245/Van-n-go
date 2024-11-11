insert into app_user (id, username, mail, password, language) values
    (1, 'JuanClient', 'juanC@mail.com', '123321', 'SPANISH');

insert into driver (id) values (1);

insert into vehicle (id, driver_id, plate_number, volume_m3, description, hourly_rate)
    values (1, 1, 'AAA999', 100, 'hola', 100);

insert into vehicle_zone (vehicle_id, zone_id) values (1, 1);

insert into vehicle_availability (id, week_day, vehicle_id, shift_period) values
-- miércoles
    (10, 'WEDNESDAY', 1, 'EVENING'),
-- jueves
    (2, 'THURSDAY', 1, 'AFTERNOON'),
    (3, 'THURSDAY', 1, 'EVENING'),
-- viernes
    (4, 'FRIDAY', 1, 'MORNING'),
    (5, 'FRIDAY', 1, 'AFTERNOON'),
    (6, 'FRIDAY', 1, 'EVENING')
;
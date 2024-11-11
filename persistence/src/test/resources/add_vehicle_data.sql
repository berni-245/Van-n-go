insert into app_user (id, username, mail, password, language, creation_time) values
    (500, 'THEDriver', 'driver@mail.com', '123321', 'SPANISH', '2024-11-7 12:00:00'),
    (501, 'YetAnotherDriver', 'YAdriver@mail.com', '123321', 'SPANISH', '2024-11-7 12:00:00'),
    (502, 'NoVehicleDriver', 'NVdriver@mail.com', '123321', 'SPANISH', '2024-11-7 12:00:00')
;

insert into driver (id, description, cbu, rating) values
    (500, 'Im the driver', '0720072007200720072044', 4.3),
    (501, 'Im another driver', '4848484848484848484899', 3.5),
    (502, 'I shall find my vehicle', '4848484848484848484811', null)
;

insert into vehicle (id, driver_id, plate_number, volume_m3, description, hourly_rate) values
    (500, 500, 'AAA999', 100, 'this car is big', 100),
    (501, 501, 'BBB999', 100, 'this car is kinda big', 100),
    (502, 500, 'CCC999', 100, 'my last car', 100),
    (503, 501, 'DDD999', 100, 'a', 100)
;

insert into vehicle_zone (vehicle_id, zone_id) values
    (500, 1),
    (501, 1),
    (502, 1)
;

insert into vehicle_availability (id, week_day, vehicle_id, shift_period) values
    -- miércoles
    (1, 'WEDNESDAY', 500, 'EVENING'),
    (2, 'WEDNESDAY', 501, 'EVENING'),
    (3, 'WEDNESDAY', 502, 'EVENING')
;
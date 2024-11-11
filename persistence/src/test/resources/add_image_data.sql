insert into app_user (id, username, mail, password, language, creation_time)
values (1, 'JuanClient', 'juanC@mail.com', '123321', 'SPANISH', '2024-11-7 12:00:00'),
       (2, 'PedroDriver', 'pedro@mail.com', '123321', 'SPANISH', '2024-11-7 12:00:00');


insert into client (id, zone_id) values (1, 1);
insert into driver (id) values (2);

insert into vehicle (id, driver_id, plate_number, volume_m3, description, hourly_rate)
values (1, 2, 'AAA999', 100, 'hola', 100);

insert into vehicle_zone (vehicle_id, zone_id)
values (1, 1);

insert into vehicle_availability (id, week_day, vehicle_id, shift_period)
values
    -- miércoles
    (1, 'WEDNESDAY', 1, 'EVENING')
;

insert into booking (id, date, shift_period, client_id, vehicle_id, origin_zone_id, destination_zone_id, state)
values (500, '2030-5-1', 'EVENING', 1, 1, 1, 20, 'ACCEPTED')  -- will be with booking id 500
;
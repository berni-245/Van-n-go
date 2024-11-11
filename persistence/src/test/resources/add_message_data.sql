insert into app_user (id, username, mail, password, language, creation_time) values
    (1, 'JuanClient', 'juanC@mail.com', '123321', 'SPANISH', '2024-11-7 12:00:00'),
    (2, 'JuanDriver', 'juanD@mail.com', '123321', 'SPANISH', '2024-11-7 12:00:00'),
    (3, 'AnotherJuanClient', 'AjuanC@mail.com', '123321', 'SPANISH', '2024-11-7 12:00:00')
;

insert into client (id, zone_id) values (1, 1), (3, 1);
insert into driver (id) values (2);

insert into message (id, content, client_id, driver_id, sent_by_driver, time_sent) values
    (500, 'hello, Ive sent you the proof of payment', 1, 2, false, '2024-11-8 12:00:00'),
    (501, 'hello, thanks!', 1, 2, true, '2024-11-8 13:00:00'),
    (502, 'cya!', 1, 2, false, '2024-11-8 14:00:00')
;
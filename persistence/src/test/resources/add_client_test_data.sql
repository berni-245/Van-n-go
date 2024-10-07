insert into app_user (id, username, mail, password) values
    (1, 'JuanClient', 'juanC@mail.com', '123321'),
    (500, 'AnotherJuanClient', 'AjuanC@mail.com', '123321');

insert into client (user_id) values
    (500);
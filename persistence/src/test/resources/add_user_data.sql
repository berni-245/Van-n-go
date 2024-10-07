insert into app_user (id, username, mail, password)
values (1,'Fletero','fletero@mail.com','fleteroPassword');
insert into app_user (id,username,mail,password)
values (2,'cliente','cliente@mail.com','password');
insert into driver (user_id) values (1);
insert into client (user_id) values (2);
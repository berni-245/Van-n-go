-- This code is used to alter tables in productions to avoid dropping and recreating them

alter table app_user add column pfp int references image (id) on delete set null;
alter table vehicle add column img_id int references image (id) on delete set null;
alter table vehicle add column hourly_rate double precision;
alter table reservation add column proof_of_payment int references image (id) on delete set null;
alter table booking add column rating int;
alter table booking add column review varchar(255);
alter table driver add column rating double precision;
alter table driver add column cbu varchar(32);
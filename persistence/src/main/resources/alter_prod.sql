-- This code is used to alter tables in productions to avoid dropping and recreating them

alter table app_user add column pfp int references image (id) on delete set null;
alter table vehicle add column img_id int references image (id) on delete set null;
alter table reservation add column proof_of_payment int references image (id) on delete set null;
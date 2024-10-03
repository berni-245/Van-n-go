-- These queries are used to alter tables in production to avoid having to drop and recreate them.

alter table app_user
    add column pfp int references image (id) on delete set null;
alter table vehicle
    add column img_id int references image (id) on delete set null;

alter table driver
    add column rating double precision;

alter table weekly_availability
    rename to weekly_availability_old;
alter table weekly_availability_old
    alter column t_start type time,
    alter column t_end type time;
create table if not exists weekly_availability
(
    id            serial primary key,
    week_day      int not null,
    hour_block_id int not null references hour_block (id) on delete cascade,
    zone_id       int not null references zone (id) on delete cascade,
    vehicle_id    int not null references vehicle (id) on delete cascade,
    unique (week_day, hour_block_id, zone_id, vehicle_id)
);
insert into weekly_availability (week_day, hour_block_id, zone_id, vehicle_id)
select distinct wa.week_day,
                hb.id,
                vwz.zone_id,
                vwz.vehicle_id
from vehicle_weekly_zone vwz
         join weekly_availability_old wa on vwz.availability_id = wa.id
         join hour_block hb
              on extract(hour from hb.t_start) >= extract(hour from wa.t_start) and
                 extract(hour from hb.t_end) <= extract(hour from wa.t_end) and
                 hb.t_end != '00:00'
order by vehicle_id, zone_id, week_day, hb.id
on conflict do nothing;

alter table booking
    rename to booking_old;

alter table reservation
    rename to reservation_old;

create type state as enum('pending', 'accepted', 'rejected', 'finished');

create table if not exists booking
(
    id               serial primary key,
    date             date,
    hour_start_id    int     not null references hour_block (id) on delete cascade,
    hour_end_id      int     not null references hour_block (id) on delete cascade,
    client_id        int     not null references client (user_id) on delete cascade,
    vehicle_id       int     not null references vehicle (id) on delete cascade,
    state            state   not null,
    proof_of_payment int     references image (id) on delete set null,
    rating           int,
    review           text
);

insert into booking (date, hour_start_id, hour_end_id, client_id, vehicle_id, state)
select bo.date,
       min(wa.hour_block_id) as hour_start_id,
       min(wa.hour_block_id) as hour_end_id,
       re.client_id,
       min(ve.id)            as vehicle_id,
       CASE
           WHEN re.is_confirmed THEN
               CASE
                   WHEN bo.date >= CURRENT_DATE THEN 'accepted'::state
                   ELSE 'finished'::state
                   END
           ELSE
               CASE
                   WHEN bo.date >= CURRENT_DATE THEN 'pending'::state
                   ELSE 'rejected'::state
                   END
           END as state
from booking_old bo
         join reservation_old re on bo.id = re.booking_id
         join vehicle ve on re.driver_id = ve.driver_id
         join weekly_availability wa on ve.id = wa.vehicle_id
where wa.week_day = (select case
                                when extract(dow from bo.date) = 0 then 7
                                else extract(dow from bo.date) end)
group by bo.id, bo.date, client_id, re.is_confirmed
order by date
;
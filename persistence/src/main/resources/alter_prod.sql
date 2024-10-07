-- These queries are used to alter tables in production to avoid having to drop and recreate them.

alter table app_user
    add column pfp int references image (id) on delete set null;
alter table vehicle
    add column img_id int references image (id) on delete set null;
alter table vehicle
    add column hourly_rate double precision not null default 0;
alter table driver
    add column rating double precision;
alter table driver
    add column CBU varchar(32);

alter table vehicle_weekly_zone rename to vehicle_weekly_zone_old;

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
from vehicle_weekly_zone_old vwz
         join weekly_availability_old wa on vwz.availability_id = wa.id
         join hour_block hb
              on extract(hour from hb.t_start) >= extract(hour from wa.t_start) and
                 extract(hour from hb.t_end) <= extract(hour from wa.t_end) and
                 hb.t_end != '00:00'
order by vehicle_id, zone_id, week_day, hb.id
on conflict do nothing;

insert into weekly_availability (week_day, hour_block_id, zone_id, vehicle_id)
select   wa.week_day,
         hb.id,
         vwz.zone_id,
         vwz.vehicle_id
from weekly_availability_old wa join hour_block hb on extract(hour from wa.t_start) = extract(hour from hb.t_start)
                                join vehicle_weekly_zone_old vwz on wa.id = vwz.availability_id
where extract(hour from wa.t_start) = extract(hour from wa.t_end)
on conflict do nothing;

alter table booking
    rename to booking_old;

alter table reservation
    rename to reservation_old;

create type state as enum ('PENDING', 'ACCEPTED', 'REJECTED', 'FINISHED');

create table if not exists booking
(
    id               serial primary key,
    date             date,
    hour_start_id    int     not null references hour_block (id) on delete cascade,
    hour_end_id      int     not null references hour_block (id) on delete cascade,
    client_id        int     not null references client (user_id) on delete cascade,
    vehicle_id       int     not null references vehicle (id) on delete cascade,
    zone_id          int     not null references zone (id) on delete cascade,
    state            state   not null,
    proof_of_payment int     references image (id) on delete set null,
    rating           int,
    review           text,
    job_description varchar(255)
);

insert into booking (date, hour_start_id, hour_end_id, client_id, vehicle_id, zone_id, state)
select bo.date,
       min(wa.hour_block_id) as hour_start_id,
       min(wa.hour_block_id) as hour_end_id,
       re.client_id,
       ve.id            as vehicle_id,
       wa.zone_id,
       CASE
           WHEN re.is_confirmed THEN
               CASE
                   WHEN bo.date >= CURRENT_DATE THEN 'ACCEPTED'::state
                   ELSE 'FINISHED'::state
                   END
           ELSE
               CASE
                   WHEN bo.date >= CURRENT_DATE THEN 'PENDING'::state
                   ELSE 'REJECTED'::state
                   END
           END as state
from booking_old bo
         join reservation_old re on bo.id = re.booking_id
         join vehicle ve on re.driver_id = ve.driver_id
         join weekly_availability wa on ve.id = wa.vehicle_id
where wa.week_day = extract(dow from bo.date)
  and ve.id = (select min(ve2.id)
               from vehicle ve2
                        join weekly_availability wa2 on ve2.id = wa2.vehicle_id
               where ve2.driver_id = ve.driver_id
                 and wa2.week_day = wa.week_day)

  and wa.zone_id = (select min(wa3.zone_id)
                    from weekly_availability wa3
                    where wa3.vehicle_id = ve.id
                      and wa3.week_day = wa.week_day
)

group by bo.id, bo.date, client_id, re.is_confirmed, ve.id, wa.zone_id
order by date
;

UPDATE weekly_availability
SET week_day = 0
WHERE week_day = 7;

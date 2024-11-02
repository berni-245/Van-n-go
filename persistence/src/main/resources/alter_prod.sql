create type state as enum ('PENDING', 'ACCEPTED', 'REJECTED', 'FINISHED');

UPDATE weekly_availability
SET week_day = 0
WHERE week_day = 7;

-- create type ShiftPeriod as enum ('MORNING', 'AFTERNOON', 'EVENING');
-- create type WeekDay as enum ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY');

-- to migrate data
alter table booking
    rename to booking_old2;

-- to revert changes to you can still use the UI
alter table booking_old2
    rename to booking;

alter table client
    rename column user_id to id;

alter table driver
    rename column user_id to id;

create table if not exists vehicle_zone
(
    vehicle_id int not null references vehicle (id) on delete cascade,
    zone_id    int not null references zone (id) on delete cascade,
    primary key (vehicle_id, zone_id)
);

create table if not exists vehicle_availability
(
    id           serial primary key,
    vehicle_id   int  not null references vehicle (id) on delete cascade,
    week_day     text not null,
    shift_period text not null,
    unique (vehicle_id, week_day, shift_period)
);

create table if not exists booking
(
    id                  serial primary key,
    date                date,
    shift_period        text  not null,
    client_id           int   not null references client (id) on delete cascade,
    vehicle_id          int   not null references vehicle (id) on delete cascade,
    origin_zone_id      int   not null references zone (id) on delete cascade,
    destination_zone_id int references zone (id) on delete cascade,
    state               text not null,
    proof_of_payment    int   references image (id) on delete set null,
    rating              int,
    review              text,
    job_description     text
);

insert into vehicle_zone (vehicle_id, zone_id)
select distinct vehicle_id, zone_id
from weekly_availability
order by vehicle_id, zone_id;

insert into vehicle_availability (vehicle_id, week_day, shift_period)
select distinct vehicle_id,
                CASE
                    WHEN week_day = 0 THEN 'SUNDAY'--::WeekDay
                    WHEN week_day = 1 THEN 'MONDAY'--::WeekDay
                    WHEN week_day = 2 THEN 'TUESDAY'--::WeekDay
                    WHEN week_day = 3 THEN 'WEDNESDAY'--::WeekDay
                    WHEN week_day = 4 THEN 'THURSDAY'--::WeekDay
                    WHEN week_day = 5 THEN 'FRIDAY'--::WeekDay
                    WHEN week_day = 6 THEN 'SATURDAY'--::WeekDay
                    END as week_day,
                case
                    when t_start >= '04:00:00' and t_end <= '12:00:00' then 'MORNING'--::ShiftPeriod
                    when t_start >= '12:00:00' and t_end <= '16:00:00' then 'AFTERNOON'--::ShiftPeriod
                    when (t_start >= '16:00:00' and (t_end <= '23:59:59' or t_end = '00:00:00'))
                        or (t_start >= '00:00:00' and t_end <= '04:00:00') then 'EVENING'--::ShiftPeriod
                    end as shift_period
from weekly_availability
         join hour_block on weekly_availability.hour_block_id = hour_block.id
order by vehicle_id, week_day;

insert into booking (date, shift_period, client_id, vehicle_id, origin_zone_id, state, proof_of_payment, rating, review,
                     job_description)
select date,
       case -- esto funciona porque los intervalos son de una hora
           when start_block.t_start >= '04:00:00' and start_block.t_start < '12:00:00' then 'MORNING'--::ShiftPeriod
           when start_block.t_start >= '12:00:00' and start_block.t_start < '20:00:00' then 'AFTERNOON'--::ShiftPeriod
           when (start_block.t_start >= '16:00:00' and start_block.t_start < '23:59:59')
               or (start_block.t_start >= '00:00:00' and start_block.t_start < '04:00:00') then 'EVENING'--::ShiftPeriod
           end as shift_period,
       client_id,
       vehicle_id,
       zone_id,
       CAST(state AS text),
       proof_of_payment,
       rating,
       review,
       job_description
from booking_old2 bo2
         join hour_block start_block on bo2.hour_start_id = start_block.id
         join hour_block end_block on bo2.hour_end_id = end_block.id
order by bo2.id;

-- alter table client rename column user_id to id;
-- alter table driver rename column user_id to id;

ALTER TABLE vehicle_availability ALTER COLUMN shift_period DROP DEFAULT;
ALTER TABLE vehicle_availability ALTER COLUMN shift_period TYPE text USING shift_period::text;

ALTER TABLE vehicle_availability ALTER COLUMN week_day DROP DEFAULT;
ALTER TABLE vehicle_availability ALTER COLUMN week_day TYPE text USING week_day::text;

ALTER TABLE booking ALTER COLUMN shift_period DROP DEFAULT;
ALTER TABLE booking ALTER COLUMN shift_period TYPE text USING shift_period::text;

ALTER TABLE booking ALTER COLUMN state DROP DEFAULT;
ALTER TABLE booking ALTER COLUMN state TYPE text USING state::text;


ALTER SEQUENCE booking_id_seq1 RENAME TO old_booking_id_seq;
ALTER SEQUENCE booking_id_seq2 RENAME TO old_booking_id_seq2;
ALTER TABLE booking_old ALTER COLUMN id SET DEFAULT nextval('old_booking_id_seq'::regclass);
ALTER TABLE booking_old2 ALTER COLUMN id SET DEFAULT nextval('old_booking_id_seq2'::regclass);
ALTER TABLE booking ALTER COLUMN id SET DEFAULT nextval('booking_id_seq'::regclass);
SELECT setval('booking_id_seq', (SELECT MAX(id) FROM booking));

-- Esta consulta da un "unsafe query" pues va a recorrer toda la tabla, y podria modificar todos los plate_number
-- pero eso es lo que queremos.
UPDATE vehicle
SET plate_number = UPPER(plate_number)

-- command to check which sequence is mapped to a table only in psql console
/*
 select column_default
 from information_schema.columns
 where table_name = booking and column_name = id;
*/

-- command to check last value of a sequence
-- select last_value from <sequence>
create table if not exists image
(
    id        serial primary key,
    file_name text  not null,
    bin       bytea not null
);

create table if not exists app_user
(
    id       serial primary key,
    username text unique not null,
    mail     text unique not null,
    password text        not null,
    pfp      int         references image (id) on delete set null
);

create table if not exists client
(
    id int primary key references app_user (id) on delete cascade
    -- more client only props would go here
);

create table if not exists driver
(
    id int primary key references app_user (id) on delete cascade,
    description  text,
    rating  double precision,
    cbu     varchar(32)
);

create table if not exists vehicle
(
    id           serial primary key,
    driver_id    int              not null references driver (id) on delete cascade,
    plate_number text unique,
    volume_m3    double precision,
    description  text,
    img_id       int              references image (id) on delete set null,
    hourly_rate  double precision not null default 0
);

create table if not exists country
(
    id   serial primary key,
    name text unique       not null,
    code varchar(2) unique not null
);

create table if not exists province
(
    id   serial primary key,
    name text unique not null
);

create table if not exists neighborhood
(
    id   serial primary key,
    name text unique not null
);

create table if not exists zone
(
    id              serial primary key,
    country_id      int references country (id) on delete cascade,
    province_id     int references province (id) on delete cascade,
    neighborhood_id int references neighborhood (id) on delete cascade,
    unique (country_id, province_id, neighborhood_id)
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

create or replace view minimal_price as SELECT driver_id, MIN(hourly_rate) from vehicle group by driver_id;
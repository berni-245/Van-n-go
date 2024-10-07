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
    user_id int primary key references app_user (id) on delete cascade
    -- more client only props would go here
);

create table if not exists driver
(
    user_id int primary key references app_user (id) on delete cascade,
    extra1  text,
    rating  double precision,
    CBU     varchar(32)
);

create table if not exists vehicle
(
    id           serial primary key,
    driver_id    int              not null references driver (user_id) on delete cascade,
    plate_number text unique,
    volume_m3    double precision,
    description  text,
    img_id       int              references image (id) on delete set null,
    hourly_rate  double precision not null default 0
);

create table if not exists weekly_availability
(
    id            serial primary key,
    week_day      int not null,
    hour_block_id int not null references hour_block (id) on delete cascade,
    zone_id       int not null references zone (id) on delete cascade,
    vehicle_id    int not null references vehicle (id) on delete cascade,
    unique (week_day, hour_block_id, zone_id, vehicle_id)
);

create table if not exists hour_block
(
    id      serial primary key,
    t_start time,
    t_end   time,
    unique (t_start, t_end),
    constraint hour_blocks_start_time_on_the_hour check (
        extract(minute from t_start) = 0 and extract(second from t_start) = 0
        ),
    constraint hour_blocks_one_hour_interval check (t_end = t_start + interval '1 hour')
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
    id               serial primary key,
    date             date,
    hour_start_id    int   not null references hour_block (id) on delete cascade,
    hour_end_id      int   not null references hour_block (id) on delete cascade,
    client_id        int   not null references client (user_id) on delete cascade,
    vehicle_id       int   not null references vehicle (id) on delete cascade,
    state            state not null,
    proof_of_payment int   references image (id) on delete set null,
    rating           int,
    review           text,
    job_description  text
);
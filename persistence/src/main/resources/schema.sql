-- From what I've read it seems to be recommended to use the singular
-- form for table names.

-- `user` is a reserved word smh...
create table if not exists app_user
(
    id       serial primary key,
    username text        not null,
    mail     text unique not null,
    password text        not null
);

create table if not exists client
(
    user_id int primary key references app_user (id) on delete cascade
    -- more client only props would go here
);

create table if not exists driver
(
    user_id int primary key references app_user (id) on delete cascade,
    extra1  text
    -- more driver only props would go here
);

create table if not exists vehicle
(
    id           serial primary key,
    driver_id    int not null references driver (user_id) on delete cascade,
    plate_number text unique,
    volume_m3    double precision,
    description  text
);

create table if not exists weekly_availability
(
    id       serial primary key,
    week_day int not null,
    t_start  time with time zone,
    t_end    time with time zone,
    unique (week_day, t_start, t_end)
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

create table if not exists vehicle_weekly_zone
(
    vehicle_id      int not null references vehicle (id) on delete cascade,
    availability_id int not null references weekly_availability (id) on delete cascade,
    zone_id         int not null references zone (id) on delete cascade,
    primary key (vehicle_id, availability_id, zone_id)
);
